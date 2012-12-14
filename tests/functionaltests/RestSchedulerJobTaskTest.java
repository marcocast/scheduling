/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $PROACTIVE_INITIAL_DEV$
 */
package functionaltests;

//import static functionaltests.RestFuncTestConstants.DEFAULT_JOB_ID;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ow2.proactive.authentication.crypto.Credentials;
import org.ow2.proactive.scheduler.common.Scheduler;
import org.ow2.proactive.scheduler.common.SchedulerState;
import org.ow2.proactive.scheduler.common.exception.UnknownJobException;
import org.ow2.proactive.scheduler.common.job.JobId;
import org.ow2.proactive.scheduler.common.job.JobState;
import org.ow2.proactive.scheduler.common.job.JobStatus;

import functionaltests.utils.RestFuncTUtils;

public class RestSchedulerJobTaskTest extends AbstractRestFuncTestCase {

    @BeforeClass
    public static void beforeClass() throws Exception {
        try {
            RestFuncTHelper.startSchedulerWebapp();
        } catch (Exception e) {
            RestFuncTHelper.stopSchedulerWebapp();
            throw e;
        }
    }

    @AfterClass
    public static void afterClass() {
        RestFuncTHelper.stopSchedulerWebapp();
    }

    @Before
    public void setUp() throws Exception {
        Scheduler scheduler = RestFuncTHelper.getScheduler();
        SchedulerState state = scheduler.getState();
        List<JobState> jobStates = new ArrayList<JobState>();
        jobStates.addAll(state.getPendingJobs());
        jobStates.addAll(state.getRunningJobs());
        jobStates.addAll(state.getFinishedJobs());
        for (JobState jobState : jobStates) {
            JobId jobId = jobState.getId();
            scheduler.killJob(jobId);
            scheduler.removeJob(jobId);
        }
    }

    @Test
    public void testLogin() throws Exception {
        RestFuncTestConfig config = RestFuncTestConfig.getInstance();
        String url = getResourceUrl("login");
        HttpPost httpPost = new HttpPost(url);
        StringBuilder buffer = new StringBuilder();
        buffer.append("username=").append(config.getLogin())
                .append("&password=").append(config.getPassword());
        StringEntity entity = new StringEntity(buffer.toString());
        entity.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpPost.setEntity(entity);
        HttpResponse response = executeUriRequest(httpPost);
        assertHttpStatusOK(response);
        assertContentNotEmpty(response);
    }

    @Test
    public void testLoginWithCredentials() throws Exception {
        RestFuncTestConfig config = RestFuncTestConfig.getInstance();
        Credentials credentials = RestFuncTUtils.createCredentials(
                config.getLogin(), config.getPassword(),
                RestFuncTHelper.getSchedulerPublicKey());
        String schedulerUrl = getResourceUrl("login");
        HttpPost httpPost = new HttpPost(schedulerUrl);
        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("credential",
                new ByteArrayBody(credentials.getBase64(),
                        MediaType.APPLICATION_OCTET_STREAM, null));
        httpPost.setEntity(multipartEntity);
        HttpResponse response = executeUriRequest(httpPost);
        assertHttpStatusOK(response);
        assertContentNotEmpty(response);
    }

    @Test
    public void testSubmit() throws Exception {
        String schedulerUrl = getResourceUrl("submit");
        HttpPost httpPost = new HttpPost(schedulerUrl);
        setSessionHeader(httpPost);
        File jobFile = RestFuncTHelper.getDefaultJobXmlfile();
        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("", new FileBody(jobFile,
                MediaType.APPLICATION_XML));
        httpPost.setEntity(multipartEntity);
        HttpResponse response = executeUriRequest(httpPost);
        assertHttpStatusOK(response);
        JSONObject jsonObj = toJsonObject(response);
        assertNotNull(jsonObj.get("id").toString());
    }

    @Test
    public void testSchedulerStateMyJobsOnly() throws Exception {
        submitDefaultJob();
        RestFuncTestConfig config = RestFuncTestConfig.getInstance();
        String schedulerUrl = getResourceUrl("state/myjobsonly");
        HttpGet httpGet = new HttpGet(schedulerUrl);
        setSessionHeader(httpGet);
        HttpResponse response = executeUriRequest(httpGet);
        assertHttpStatusOK(response);
        JSONObject jsonObj = toJsonObject(response);
        assertJobOwner((JSONArray) jsonObj.get("pendingJobs"),
                config.getLogin());
        assertJobOwner((JSONArray) jsonObj.get("runningJobs"),
                config.getLogin());
        assertJobOwner((JSONArray) jsonObj.get("finishedJobs"),
                config.getLogin());
    }

    @Test
    public void testLitJobs() throws Exception {
        String jobId = submitDefaultJob();
        String resource = jobId;
        String schedulerUrl = getResourceUrl("jobs/" + resource);
        HttpGet httpGet = new HttpGet(schedulerUrl);
        setSessionHeader(httpGet);
        HttpResponse response = executeUriRequest(httpGet);
        assertHttpStatusOK(response);
        JSONObject jsonObject = toJsonObject(response);
        assertJobId(resource, jsonObject);
    }

    @Test
    public void testJobResult() throws Exception {
        String jobId = submitFinishedJob();
        String resource = "jobs/" + jobId + "/result";
        String schedulerUrl = getResourceUrl(resource);
        HttpGet httpGet = new HttpGet(schedulerUrl);
        setSessionHeader(httpGet);
        HttpResponse response = executeUriRequest(httpGet);
        assertHttpStatusOK(response);
        JSONObject jsonObject = toJsonObject(response);
        String taskResult = getTaskResult(jsonObject, "Test-Job-Task");
        assertNotNull(taskResult);
    }

    @Test
    public void testJobResultValue() throws Exception {
        String jobId = submitFinishedJob();
        String resource = getResourceUrl("jobs/" + jobId + "/result/value");
        HttpGet httpGet = new HttpGet(resource);
        setSessionHeader(httpGet);
        HttpResponse response = executeUriRequest(httpGet);
        assertHttpStatusOK(response);
        JSONObject jsonObject = toJsonObject(response);
        assertEquals("TEST-JOB", jsonObject.get("Test-Job-Task").toString());
    }

    @Test(expected = UnknownJobException.class)
    public void testRemoveJob() throws Exception {
        String jobId = submitDefaultJob();
        String resource = "jobs/" + jobId;
        String schedulerUrl = getResourceUrl(resource);
        HttpDelete delete = new HttpDelete(schedulerUrl);
        setSessionHeader(delete);
        HttpResponse response = executeUriRequest(delete);
        assertHttpStatusOK(response);
        assertTrue(Boolean.valueOf(getContent(response)));
        RestFuncTHelper.getScheduler().getJobState(jobId);
        fail("UnknownJobException should be thrown");
    }

    @Test
    public void testKillJob() throws Exception {
        String jobId = submitPendingJobId();
        String resource = "jobs/" + jobId + "/kill";
        String schedulerUrl = getResourceUrl(resource);
        HttpPut httpPut = new HttpPut(schedulerUrl);
        setSessionHeader(httpPut);
        HttpResponse response = executeUriRequest(httpPut);
        assertHttpStatusOK(response);
        JobState jobState = getScheduler().getJobState(jobId);
        assertEquals(JobStatus.KILLED, jobState.getStatus());
    }

    @Test
    public void testGetJobTaskIds() throws Exception {
        String jobId = submitDefaultJob();
        String resource = "jobs/" + jobId + "/tasks";
        String schedulerUrl = getResourceUrl(resource);
        HttpGet httpGet = new HttpGet(schedulerUrl);
        setSessionHeader(httpGet);
        HttpResponse response = executeUriRequest(httpGet);
        assertHttpStatusOK(response);
        JSONArray jsonArray = toJsonArray(response);
        assertEquals("Test-Job-Task", jsonArray.get(0).toString());
    }

    @Test
    public void testJobTaskStates() throws Exception {
        String jobId = submitDefaultJob();
        String resource = "jobs/" + jobId + "/taskstates";
        String schedulerUrl = getResourceUrl(resource);
        HttpGet httpGet = new HttpGet(schedulerUrl);
        setSessionHeader(httpGet);
        HttpResponse response = executeUriRequest(httpGet);
        assertHttpStatusOK(response);
        JSONArray jsonArray = toJsonArray(response);
        assertTrue(jsonArray.size() > 0);
    }

    private void assertJobId(String expected, JSONObject job) {
        JSONObject jobInfo = (JSONObject) job.get("jobInfo");
        JSONObject jobId = (JSONObject) jobInfo.get("jobId");
        String actual = jobId.get("id").toString();
        assertTrue(actual.equals(expected));
    }

    private void assertJobOwner(JSONArray jobs, String owner) {
        for (int i = 0; i < jobs.size(); i++) {
            JSONObject job = (JSONObject) jobs.get(i);
            assertTrue(job.get("owner").equals(owner));
        }
    }

    private String getTaskResult(JSONObject job, String taskName) {
        Object allResults = job.get("allResults");
        Object result = ((JSONObject) allResults).get(taskName);
        return ((JSONObject) result).get("serializedValue").toString();
    }

}