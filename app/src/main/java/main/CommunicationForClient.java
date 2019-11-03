/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package main;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.feedback.Activity_Login;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import newdbclass.Criterion;
import newdbclass.Project;
import newdbclass.Remark;
import newdbclass.Student;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.JSONUtil;

public class CommunicationForClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private String host;
    private OkHttpClient client;
    private String token;
    AllFunctions functions;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("audio/mpeg");

    public CommunicationForClient(AllFunctions functions) {
//        host = "http://10.13.88.39:8080/RapidFeedback/";
//        host = "http://121.221.0.43:8080/RapidFeedback/";
        host = "http://192.168.0.7:8080/RapidFeedback/";
        client = new OkHttpClient();
        this.functions = functions;
    }

    public void register(String firstName, String middleName, String lastName,
                         String email, String password) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("firstName", firstName);
        jsonSend.put("middleName", middleName);
        jsonSend.put("lastName", lastName);
        jsonSend.put("email", email);
        jsonSend.put("password", password);

        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/RegisterServlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            int register_ACK = Integer.parseInt(jsonReceive.get("id").toString());
            functions.registerACK(register_ACK);
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void login(String username, String password) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("username", username);
        jsonSend.put("password", password);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/LoginServlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONUtil.write(receive);
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            int login_ACK = Integer.parseInt(jsonReceive.get("login_ACK").toString());
            if (login_ACK > 0) {
                Activity_Login.mUserInfoOpertor.saveUserInfo(username, password);
                //get projectlist from jsonReceive
                String projectListString = jsonReceive.get("projectList").toString();
                JSONUtil.write(projectListString);
                String firstName = jsonReceive.getString("firstName");
                List<Project> projectList = JSONObject.parseArray(projectListString, Project.class);
                ArrayList<Project> arrayList = new ArrayList();
                arrayList.addAll(projectList);
                functions.setUsername(firstName);
                functions.setUserEmail(username);
                token = jsonReceive.getString("token");
                functions.setId(login_ACK);
                functions.loginACK(login_ACK);
            } else {
                functions.loginACK(login_ACK);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void syncProjectList(int id) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("userId", id);
        jsonSend.put("token", token);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/SyncProjectListServlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            //get projectlist from jsonReceive
            String projectListString = jsonReceive.get("projectList").toString();
            List<Project> projectList = JSONObject.parseArray(projectListString, Project.class);
            ArrayList<Project> arrayList = new ArrayList();
            arrayList.addAll(projectList);
            Boolean sync_ACK = Boolean.parseBoolean(jsonReceive.get("syn_ACK").toString());
            functions.syncACK(sync_ACK, arrayList);
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void updateProjectAbout(String projectName, String subjectName,
                                   String subjectCode, String description,
                                   int durationSec, int warningSec, int userId, int projectId) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectName", projectName);
        jsonSend.put("subjectName", subjectName);
        jsonSend.put("subjectCode", subjectCode);
        jsonSend.put("description", description);
        jsonSend.put("durationSec", durationSec);
        jsonSend.put("warningSec", warningSec);
        jsonSend.put("principalId", userId);
        jsonSend.put("id", projectId);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/UpdateProject_About_Servlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean updateProject_ACK = Boolean.parseBoolean(jsonReceive.get("updateProject_ACK").toString());
            int returnedProjectId = Integer.parseInt(jsonReceive.get("projectId").toString());
            functions.setAboutACK(updateProject_ACK, returnedProjectId);
        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void deleteProject(int projectId) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/DeleteProjectServlet")
                .post(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean updateStudent_ACK = Boolean.parseBoolean(jsonReceive.get("updateProject_ACK").toString());
            functions.deleteACK(updateStudent_ACK);
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void criteriaListSend(ArrayList<Criterion> markedCriteriaList, int projectId) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        String markedCriteriaListString = com.alibaba.fastjson.JSON.toJSONString(markedCriteriaList);
        jsonSend.put("criterionList", markedCriteriaListString);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/CriteriaListServlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean updateProject_ACK = Boolean.parseBoolean(jsonReceive.get("updateProject_ACK").toString());
            functions.updateProjectCriteriaACK(updateProject_ACK);
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void inviteMarker(int markerId, int projectId) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        jsonSend.put("markerId", markerId);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/InviteAssessorServlet")
                .post(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            int invite_ACK = Integer.parseInt(jsonReceive.get("invite_ACK").toString());
            AllFunctions.getObject().inviteMarkerACK(invite_ACK);

        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void deleteMarker(int markerId, int projectId) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        jsonSend.put("markerId", markerId);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/InviteAssessorServlet")
                .delete(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean delete_ACK = Boolean.parseBoolean(jsonReceive.get("delete_ACK").toString());
            AllFunctions.getObject().deleteMarkerACK(delete_ACK);

        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void addStudent(int projectId, ArrayList<Student> studentList, String action) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        jsonSend.put("studentList", com.alibaba.fastjson.JSON.toJSONString(studentList));
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/AddStudentServlet")
                .post(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            int updateStudent_ACK = Integer.parseInt(jsonReceive.get("updateStudent_ACK").toString());
            functions.addStudentACK(updateStudent_ACK, action);
        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void editStudent(int studentId, int studentNumber, String firstName,
                            String middleName, String lastName, String email, int groupNumber, int projectId) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("studentId", studentId);
        jsonSend.put("studentNumber", studentNumber);
        jsonSend.put("firstName", firstName);
        jsonSend.put("middleName", middleName);
        jsonSend.put("lastName", lastName);
        jsonSend.put("email", email);
        jsonSend.put("group", groupNumber);
        jsonSend.put("projectId", projectId);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/EditStudentServlet")
                .post(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean updateStudent_ACK = Boolean.parseBoolean(jsonReceive.get("updateStudent_ACK").toString());
            functions.editStudentACK(updateStudent_ACK);
        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void deleteStudent(int projectId, int studentId) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        jsonSend.put("studentId", studentId);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/DeleteStudentServlet")
                .post(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean updateStudent_ACK = Boolean.parseBoolean(jsonReceive.get("updateStudent_ACK").toString());
            functions.deleteStudentACK(updateStudent_ACK);
        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void sendMark(int projectId, int studentId, String remark) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        jsonSend.put("studentId", studentId);
        jsonSend.put("remark", remark);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/AddResultServlet")
                .post(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            String mark_ACK = jsonReceive.get("ACK").toString();
            functions.sendMarkACK(mark_ACK);
        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void sendEmail(int projectId, int studentId, int sendBoth) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        jsonSend.put("studentId", studentId);
        jsonSend.put("sendBoth", sendBoth);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/SendEmailServlet")
                .post(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            String sendMail_ACK = jsonReceive.get("sendMail_ACK").toString();
        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void sendFinalResult(int projectId, int studentId, double finalScore, String finalRemark) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        jsonSend.put("studentId", studentId);
        jsonSend.put("finalScore", String.format("%.2f", finalScore));
        jsonSend.put("finalRemark", finalRemark);
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/FinalResultServlet")
                .post(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean sendFinalResultACK = Boolean.parseBoolean(jsonReceive.get("ACK").toString());
            functions.sendFinalResultACK(sendFinalResultACK);
        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void submitFile(int projectId, int studentId, String path) {
        //test a existed file
        // File f = new File(Environment.getExternalStorageDirectory()+"/SoundRecorder"+"/My Recording_7.mp4");
        File f = new File(path);
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, f);
        MultipartBody multipartBody = new MultipartBody.Builder()
                // set type as "multipart/form-data"，otherwise cannot upload file
                .setType(MultipartBody.FORM)
                .addFormDataPart("filename", projectId + "_" + studentId, body)
                .build();
        Request request = new Request.Builder()
                .url(host + "AudioRecorderServlet")
                .post(multipartBody)
                .build();

        //callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("get back Parameter：\n" + response.body().string());
            }
        });
    }
}