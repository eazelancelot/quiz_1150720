package com.example.quiz_1150720.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.quiz_1150720.constant.QuizResponseMessage;
import com.example.quiz_1150720.dto.QuizDeleteRequest;
import com.example.quiz_1150720.dto.QuizRequest;
import com.example.quiz_1150720.dto.QuizResponse;
import com.example.quiz_1150720.entity.Question;
import com.example.quiz_1150720.entity.QuestionOption;
import com.example.quiz_1150720.entity.Quiz;
import com.example.quiz_1150720.enums.QuestionType;
import com.example.quiz_1150720.repository.QuizRepository;

@Service
public class QuizService {

    // 使用 final 宣告，透過建構方法注入 (Constructor Injection)，確保依賴不可變且便於單元測試
    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    /**
     * 1. 創建問卷邏輯
     */
    @Transactional
    public QuizResponse createQuiz(QuizRequest request) {
        // 執行自定義的業務與題型選項檢查
        validateQuizLogic(request);
        
        // 將 Request DTO 轉換為 JPA Entity
        Quiz quiz = convertRequestToEntity(request);
        
        // 透過 JpaRepository 儲存問卷
        // 搭配 Entity 上的 CascadeType.ALL 與 orphanRemoval，會連同底下的問題與選項一起寫入資料庫
        // save(quiz): 因為 quiz 中的 id 不存在於 DB，所以會執行新增
        Quiz savedQuiz = quizRepository.save(quiz);
        
        // 將儲存後的 Entity 轉換回 Response DTO 回傳
        return convertEntityToResponse(savedQuiz);
    }

    /**
     * 2. 更新問卷邏輯
     */
    @Transactional
    public QuizResponse updateQuiz(QuizRequest request) {
        // 檢查問卷 ID 是否存在於資料庫中，若不存在則拋出集中管理的例外訊息
        if (request.getId() == null || !quizRepository.existsById(request.getId())) {
            throw new RuntimeException(QuizResponseMessage.QUIZ_NOT_FOUND_FOR_UPDATE);
        }
        
        // 執行自定義的業務與題型選項防禦性檢查
        validateQuizLogic(request);
        
        // 將 Request 轉為 Entity（JPA 遇到已有 ID 的物件會執行 Update）
        Quiz quiz = convertRequestToEntity(request);
        
        // 儲存更新後的問卷（若題目或選項在清單中被移除，會因 orphanRemoval = true 自動被刪除）
        // save(quiz): 因為 quiz 中的 id 已存在於 DB，所以會執行更新
        Quiz updatedQuiz = quizRepository.save(quiz);
        
        return convertEntityToResponse(updatedQuiz);
    }

    /**
     * 3. 取得所有問卷列表邏輯
     */
    public List<QuizResponse> getAllQuizzes() {
        // 呼叫 Repository 中的原生 SQL 查詢所有問卷
        List<Quiz> quizzes = quizRepository.findAllQuizzesNative();
        
        // 將 Entity 清單透過 Stream API 轉換為 Response DTO 清單
        return quizzes.stream()
                .map(this::convertEntityToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 4. 根據問卷 ID 取得單筆問卷詳情邏輯
     */
    public QuizResponse getQuizById(Long id) {
        // 呼叫 Repository 中的原生 SQL 根據 ID 查詢問卷
        Quiz quiz = quizRepository.findQuizByIdNative(id);
        
        // 若找不到該問卷則拋出例外
        if (quiz == null) {
            throw new RuntimeException(QuizResponseMessage.QUIZ_NOT_FOUND);
        }
        
        return convertEntityToResponse(quiz);
    }
    
    /**
     * 5. 根據問卷 ID 刪除問卷以及底下的所有問題和問題選項
     * 交易邊界建立在 Service 層！
     * 當進入這個方法時，Spring 會自動開啟一個 Transaction。
     * 裡面執行的 deleteQuizzesByIdsNative 就能夠安全地進行資料庫刪除異動。
     */
    @Transactional
    public void deleteQuizzes(QuizDeleteRequest request) {
        List<Long> ids = request.getIds();

        if (ids == null || ids.isEmpty()) {
            return;
        }

        quizRepository.deleteQuizzesByIdsNative(ids);
    }

    // ================ private 方法
    /**
     * 【程式碼檢查邏輯】：
     * 1. 檢查結束日期是否早於開始日期。
     * 2. 針對不同題型（SINGLE, MULTIPLE, TEXT）檢查選項是否合規。
     */
    private void validateQuizLogic(QuizRequest request) {
        // 檢查日期合理性：結束日期不得早於開始日期(兩個日期皆不得為 null 已透過 Validation 檢查了)     
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException(QuizResponseMessage.END_DATE_BEFORE_START_DATE);
        }       

        // 若無問題清單則直接返回
        if (request.getQuestions() == null) return;

        // 迴圈檢查每個問題的細節
        for (QuizRequest.QuestionRequest qReq : request.getQuestions()) {
            QuestionType type = qReq.getType();

            // 若為「單選」或「多選」題
            if (type == QuestionType.SINGLE || type == QuestionType.MULTIPLE) {
                // 必須包含選項，且選項不可為空
                if (qReq.getOptions() == null || qReq.getOptions().isEmpty()) {
                    throw new RuntimeException(String.format(QuizResponseMessage.OPTION_REQUIRED_FOR_CHOICE, qReq.getTitle()));
                }
                // 檢查每個選項的編號與內容是否完整
                for (QuizRequest.QuestionOptionRequest oReq : qReq.getOptions()) {
                    if (oReq.getOptionNo() == null) {
                        throw new RuntimeException(String.format(QuizResponseMessage.OPTION_NO_NOT_NULL_MSG, qReq.getTitle()));
                    }
                    // StringUtils.hasText(): 判斷不是 1.null 或 2.空字串 或 3.全空白字串 就會返回 true
                    if(!StringUtils.hasText(oReq.getOptionText())) {
                    	throw new RuntimeException(String.format(QuizResponseMessage.OPTION_TEXT_NOT_BLANK_MSG, qReq.getTitle()));
                    }
                }
            } 
            // 若為「簡答」題
            else if (type == QuestionType.TEXT) {
                // 簡答題不得包含選項
                if (qReq.getOptions() != null && !qReq.getOptions().isEmpty()) {
                    throw new RuntimeException(String.format(QuizResponseMessage.OPTION_NOT_ALLOWED_FOR_TEXT, qReq.getTitle()));
                }
            }
        }
    }

    /**
     * 【轉換工具】：QuizRequest (DTO) ➔ Quiz (Entity)
     * 
     * 【為什麼 DTO 轉換時需要保留並設定 id？】：
     * 因為本系統的 QuizRequest 同時被共用在「建立 (Create)」與「更新 (Update)」兩種情境中，
     * 在轉換時必須透過 id 來讓 JPA/Hibernate 判斷該執行 INSERT 還是 UPDATE：
     * 1. 針對 Quiz 本身：
     *    - 建立新問卷時：request.getId() 為 null，JPA 視其為新物件並執行 INSERT。
     *    - 更新問卷時：request.getId() 帶有數值，JPA 視其為既有資料並執行 UPDATE。
     * 
     * 2. 針對 Question 與 QuestionOption（子物件）：
     *    - 既有項目：帶有原本的資料庫流水號 id，JPA 會進行欄位更新。
     *    - 新增項目：id 為 null（使用者在修改問卷時新加的題目或選項），JPA 會自動執行 INSERT。
     *    - 這能讓 Hibernate 精準分辨哪些是舊資料、哪些是新增加的項目，並配合 orphanRemoval 正確刪除被移除的子項目。
     */
    private Quiz convertRequestToEntity(QuizRequest request) {
        Quiz quiz = new Quiz();
        quiz.setId(request.getId());
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setStartDate(request.getStartDate());
        quiz.setEndDate(request.getEndDate());
        quiz.setIsPublished(request.getIsPublished());

        // 轉換問題清單
        if (request.getQuestions() != null) {
            List<Question> questions = request.getQuestions().stream().map(qReq -> {
                Question question = new Question();
                question.setId(qReq.getId());
                question.setQuestionNo(qReq.getQuestionNo());
                question.setTitle(qReq.getTitle());
                question.setType(qReq.getType());
                question.setIsRequired(qReq.getIsRequired());
                question.setQuiz(quiz); // 雙向關聯：子物件綁定父物件

                // 轉換選項清單
                if (qReq.getOptions() != null) {
                    List<QuestionOption> options = qReq.getOptions().stream().map(oReq -> {
                        QuestionOption option = new QuestionOption();
                        option.setId(oReq.getId());
                        option.setOptionNo(oReq.getOptionNo());
                        option.setOptionText(oReq.getOptionText());
                        option.setQuestion(question); // 雙向關聯：子物件綁定父物件
                        return option;
                    }).collect(Collectors.toList());
                    question.setOptions(options);
                }
                return question;
            }).collect(Collectors.toList());
            quiz.setQuestions(questions);
        }
        return quiz;
    }

    /**
     * 【轉換工具】：Quiz (Entity) ➔ QuizResponse (DTO)
     */
    private QuizResponse convertEntityToResponse(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setDescription(quiz.getDescription());
        response.setStartDate(quiz.getStartDate());
        response.setEndDate(quiz.getEndDate());
        response.setIsPublished(quiz.getIsPublished());

        // 轉換問題回應清單
        if (quiz.getQuestions() != null) {
            List<QuizResponse.QuestionResponse> qResponses = quiz.getQuestions().stream().map(q -> {
                QuizResponse.QuestionResponse qRes = new QuizResponse.QuestionResponse();
                qRes.setId(q.getId());
                qRes.setQuestionNo(q.getQuestionNo());
                qRes.setTitle(q.getTitle());
                qRes.setType(q.getType());
                qRes.setIsRequired(q.getIsRequired());

                // 轉換選項回應清單
                if (q.getOptions() != null) {
                    List<QuizResponse.QuestionOptionResponse> oResponses = q.getOptions().stream().map(o -> {
                        QuizResponse.QuestionOptionResponse oRes = new QuizResponse.QuestionOptionResponse();
                        oRes.setId(o.getId());
                        oRes.setOptionNo(o.getOptionNo());
                        oRes.setOptionText(o.getOptionText());
                        return oRes;
                    }).collect(Collectors.toList());
                    qRes.setOptions(oResponses);
                }
                return qRes;
            }).collect(Collectors.toList());
            response.setQuestions(qResponses);
        }
        return response;
    }
}
