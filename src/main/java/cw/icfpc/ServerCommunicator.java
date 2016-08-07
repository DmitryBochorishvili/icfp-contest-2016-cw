package cw.icfpc;

import com.google.gson.Gson;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.http.protocol.HTTP.USER_AGENT;


class rankingEntity {
    private double resemblance;
    private int solution_size;

    public double getResemblance() {
        return resemblance;
    }

    public int getSolution_size() {
        return solution_size;
    }
}

class problemsEntity {
    private ArrayList<rankingEntity> ranking;
    private BigInteger publish_time;
    private int solution_size;
    private int problem_id;
    private String owner;
    private int problem_size;
    private String problem_spec_hash;

    public ArrayList<rankingEntity> getRanking() {
        return ranking;
    }

    public BigInteger getPublish_time() {
        return publish_time;
    }

    public int getSolution_size() {
        return solution_size;
    }

    public int getProblem_id() {
        return problem_id;
    }

    public String getOwner() {
        return owner;
    }

    public int getProblem_size() {
        return problem_size;
    }

    public String getProblem_spec_hash() {
        return problem_spec_hash;
    }
}
class leaderBoardEntity {
    private int username;
    private double score;

    public int getUsername() {
        return username;
    }

    public double getScore() {
        return score;
    }
}
class usersEntity {
    private int username;
    private String display_name;

    public int getUsername() {
        return username;
    }

    public String getDisplay_name() {
        return display_name;
    }
}
class contentStatusSnapshotEntity {
    private ArrayList<problemsEntity> problems;
    private BigInteger snapshot_time;
    private ArrayList<leaderBoardEntity> leaderboard;
    private ArrayList<usersEntity> users;

    public ArrayList<problemsEntity> getProblems() {
        return problems;
    }

    public BigInteger getSnapshot_time() {
        return snapshot_time;
    }

    public ArrayList<leaderBoardEntity> getLeaderboard() {
        return leaderboard;
    }

    public ArrayList<usersEntity> getUsers() {
        return users;
    }
}

class snapshotHashes {
    private BigInteger snapshot_time;
    private String snapshot_hash;

    public BigInteger getSnapshot_time() {
        return snapshot_time;
    }

    public String getSnapshot_hash() {
        return snapshot_hash;
    }
}

class snapshotHashesEntity {
    private String ok;
    private ArrayList<snapshotHashes> snapshots;

    public String getOk() {
        return ok;
    }

    public ArrayList<snapshotHashes> getSnapshots() {
        return snapshots;
    }
}

public class ServerCommunicator {


    static final int requestTimeOut = 1000;
    static final String teamApiKey = "185-0286b03fb0e28503bd4456ac3b9825af";
    public static void hello() {
        System.out.println(callAPIMethod("hello"));
    }

    public static snapshotHashesEntity getSnapshotHashes() {
        String snapshotsList = callAPIMethod("snapshot/list");
        System.out.print(snapshotsList);

        Gson gson = new Gson();

        snapshotHashesEntity entity = gson.fromJson(snapshotsList, snapshotHashesEntity.class);
        return entity;

    }

    public static contentStatusSnapshotEntity blobLookup(String hash) {
        String exampleHash = "89c4c40226f4efcdb8fc58c9f74339d768ef4737";

        String snapshot = callAPIMethod("blob/" + (hash == null ? exampleHash : hash));

        System.out.print(snapshot);
        Gson gson = new Gson();
        contentStatusSnapshotEntity entity = gson.fromJson(snapshot, contentStatusSnapshotEntity.class);

        return entity;
    }

    public static String getTask(String taskHash) {
        String result = callAPIMethod("blob/" + taskHash);
        System.out.print(result);
        return result;
    }

    public static String submitSolution(String taskId, String solution) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("problem_id", taskId));
        params.add(new BasicNameValuePair("solution_spec", solution));

        String result = callAPIMethod("solution/submit", params);
        System.out.print(result);
        return result;
    }

    private static String callAPIMethod(String apiCall) {
        return callAPIMethod(apiCall, null);
    }

    private static String callAPIMethod(String apiCall, List<NameValuePair> params) {
        try {
            Thread.sleep(requestTimeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String url = "http://2016sv.icfpcontest.org/api/" + apiCall;

        HttpClient client = HttpClientBuilder.create().build();

        try {
            HttpUriRequest request = null;
            if(params == null || params.size() == 0) { // GET
                request = new HttpGet(url);
            }
            else {
                HttpPost post = new HttpPost(url);
                post.setEntity(new UrlEncodedFormEntity(params));
                request = post;
            }

            // add request header
            request.addHeader("X-API-Key", teamApiKey);
            HttpResponse response = null;
            response = client.execute(request);

            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
                result.append(System.getProperty("line.separator"));
        }
        return result.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }



    public static void main(String[] args) {
        hello();

        downloadAllProblems();

        //blobLookup(null);
    }

    private static void downloadAllProblems() {
        snapshotHashesEntity hashes = getSnapshotHashes();
        snapshotHashes latestSnapshotInfo = hashes.getSnapshots().get(hashes.getSnapshots().size() - 1);
        contentStatusSnapshotEntity snapshot = blobLookup(latestSnapshotInfo.getSnapshot_hash());
        new File("downloadedProblems").mkdir();
        saveProblemAuthorsInfo(snapshot.getUsers());

        snapshot.getProblems().forEach(problem -> {
            System.out.println("owner:" + problem.getOwner() + "; problem_hash: " + problem.getProblem_spec_hash());
            String directory = "downloadedProblems/" + problem.getOwner();
            new File(directory).mkdir();
            if(!new File(directory + "/" + problem.getProblem_id()).exists()) { // do not download in case if file exists
                String problemText = getTask(problem.getProblem_spec_hash());
                try {
                    PrintWriter writer = new PrintWriter(directory + "/" + problem.getProblem_id(), "UTF-8");
                    writer.println(problemText);
                    writer.close();
                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static void saveProblemAuthorsInfo(ArrayList<usersEntity> users) {
        try {
            PrintWriter writer = new PrintWriter("downloadedProblems/authors.txt", "UTF-8");
            writer.println("user_id:display_name");

            users.forEach(user -> {
                writer.println(user.getUsername() + ":" + user.getDisplay_name());
            });
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
