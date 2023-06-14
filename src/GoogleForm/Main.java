package GoogleForm;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.google.api.services.forms.v1.Forms;
import com.google.api.services.forms.v1.FormsScopes;
import com.google.api.services.forms.v1.model.*;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Year;
import java.util.*;


public class Main {

    private static final String APPLICATION_NAME = "OHSP";
    private static Drive driveService;
    private static Forms formsService;

    static {

        try {

            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            driveService = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    jsonFactory, null)
                    .setApplicationName(APPLICATION_NAME).build();

            formsService = new Forms.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    jsonFactory, null)
                    .setApplicationName(APPLICATION_NAME).build();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getAccessToken() throws IOException {
    	String basePath = System.getProperty("user.dir");
    	System.out.println(basePath);
        GoogleCredentials credential = GoogleCredentials.fromStream(Objects.requireNonNull(
                Main.class.getResourceAsStream("cred.json"))).createScoped(FormsScopes.all());
        return credential.getAccessToken() != null ?
                credential.getAccessToken().getTokenValue() :
                credential.refreshAccessToken().getTokenValue();
    }
    
    public static String createNewForm(String token) throws IOException {
        Form form = new Form();
        form.setInfo(new Info());
        int year = Integer.parseInt(Year.now().toString());
        form.getInfo().setTitle("Status Update "+year+" for Open High School Program Students");
        form = formsService.forms().create(form)
                .setAccessToken(token)
                .execute();
        return form.getFormId();
    }
    public static boolean publishForm(String formId, String token) throws GeneralSecurityException, IOException {

        PermissionList list = driveService.permissions().list(formId).setOauthToken(token).execute();

        if (list.getPermissions().stream().filter((it) -> it.getRole().equals("reader")).findAny().isEmpty()) {
            Permission body = new Permission();
            body.setRole("reader");
            body.setType("anyone");
            driveService.permissions().create(formId, body).setOauthToken(token).execute();
            return true;
        }

        return false;
    }

    public static void transformInQuiz(String formId, String token) throws IOException {
        BatchUpdateFormRequest batchRequest = new BatchUpdateFormRequest();
        Request request = new Request();
        request.setUpdateSettings(new UpdateSettingsRequest());
        request.getUpdateSettings().setSettings(new FormSettings());
        request.getUpdateSettings().getSettings().setQuizSettings(new QuizSettings());
        request.getUpdateSettings().getSettings().getQuizSettings().setIsQuiz(false);
        request.getUpdateSettings().setUpdateMask("quizSettings.isQuiz");
        batchRequest.setRequests(Collections.singletonList(request));
        formsService.forms().batchUpdate(formId, batchRequest)
                .setAccessToken(token).execute();
    }
    
    public static void addItemToQuiz(
            String questionText,
            List<String> answers,
            String correctAnswer, String questionType,
            String formId, String token) throws IOException {

        BatchUpdateFormRequest batchRequest = new BatchUpdateFormRequest();
        Request request = new Request();

        Item item = new Item();
        item.setTitle(questionText);
       
        item.setQuestionItem(new QuestionItem());
        Question question = new Question();
        question.setRequired(true);
        if(questionType.equals("RADIO")) {
           question.setChoiceQuestion(new ChoiceQuestion());
           question.getChoiceQuestion().setType(questionType);
    	   List<Option> options = new ArrayList<>();
           for (String answer : answers) {
               Option option = new Option();
               option.setValue(answer);
               options.add(option);
           }
           question.getChoiceQuestion().setOptions(options);
        }else {
          question.setTextQuestion(new TextQuestion());       
          question.getTextQuestion().setParagraph(true);
        }
        
        item.getQuestionItem().setQuestion(question);
        
        request.setCreateItem(new CreateItemRequest());
        request.getCreateItem().setItem(item);
        request.getCreateItem().setLocation(new Location());
        request.getCreateItem().getLocation().setIndex(0);

        batchRequest.setRequests(Collections.singletonList(request));

        formsService.forms().batchUpdate(formId, batchRequest)
                .setAccessToken(token).execute();
    }
    public static ListFormResponsesResponse readResponses(String formId, String token) throws IOException {
        ListFormResponsesResponse response = formsService.forms().responses().list(formId).setOauthToken(token).execute();
        return response;
    }
    
//    public static void main(String[] args) throws IOException, GeneralSecurityException {
//		
//    	String token = getAccessToken();
//    	String formId = createNewForm(token);
//        System.out.println(formId);
////        
////        readResponses("1EfuvUIOI6plUF-bei6RUK1_W3RIMkHstG8EOGT4q9Ac", token);
//
//        publishForm(formId, token);
//
//        transformInQuiz(formId, token);
//
//        addItemToQuiz(
//                 "Status",
//                 Arrays.asList("Test", "Test", "Conitnueing", "Working"),
//                 "",
//                 formId,
//                 token
//         );
//        addItemToQuiz(
//                "Detail",
//                Arrays.asList("", "", "", ""),
//                "",
//                formId,
//                token
//        );
//	}
    

}