package com.activiti.sdk.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.alfresco.activiti.conf.ApplicationIntegrationTestConfiguration;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.activiti.domain.idm.AccountType;
import com.activiti.domain.idm.Capabilities;
import com.activiti.domain.idm.Group;
import com.activiti.domain.idm.GroupCapability;
import com.activiti.domain.idm.PersistentToken;
import com.activiti.domain.idm.Tenant;
import com.activiti.domain.idm.User;
import com.activiti.domain.idm.UserStatus;
import com.activiti.domain.runtime.EmailTemplate;
import com.activiti.extension.bean.TestWorkflowUtils;
import com.activiti.repository.idm.GroupCapabilityRepository;
import com.activiti.repository.idm.GroupRepository;
import com.activiti.repository.idm.TenantEventRepository;
import com.activiti.repository.idm.TenantRepository;
import com.activiti.repository.idm.UserRepository;
import com.activiti.repository.runtime.RelatedContentRepository;
import com.activiti.repository.runtime.RuntimeAppDefinitionRepository;
import com.activiti.repository.runtime.RuntimeAppRepository;
import com.activiti.sdk.test.utils.UserBean;
import com.activiti.security.CustomPersistentRememberMeServices;
import com.activiti.security.CustomRememberMeService;
import com.activiti.service.api.GroupService;
import com.activiti.service.api.UserService;
import com.activiti.service.idm.TenantService;
import com.activiti.service.license.LicenseStatus;
import com.activiti.service.runtime.EmailTemplateService;
import com.activiti.service.runtime.EmailTemplateService.ProcessedEmailTemplate;
import com.activiti.servlet.WebConfigurer;

/**
 * 
 * @author Carlo Cavallieri, Jessica Foroni
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ApplicationIntegrationTestConfiguration.class,
		WebConfigurer.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseIntegrationTestSDK {
	private static final Logger log = LoggerFactory.getLogger(BaseIntegrationTestSDK.class);

	@Value("${server.port}")
	protected int httpServerPort;

	@Value("${server.contextPath}")
	protected String httpServerContextPath;

	@Autowired
	protected ApplicationContext appContext;

	@Autowired
	protected TenantRepository tenantRepository;
	@Autowired
	protected TenantService tenantService;
	@Autowired
	protected TenantEventRepository tenantEventRepository;
	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected UserService userService;
	@Autowired
	protected GroupRepository groupRepository;
	@Autowired
	protected GroupCapabilityRepository groupCapabilityRepository;
	@Autowired
	protected GroupService groupService;
	@Autowired
	@Qualifier("customPersistentRememberMeServices")
	protected CustomRememberMeService rememberMeService;
	@Autowired
	protected RuntimeAppRepository runtimeAppRepository;
	@Autowired
	protected RuntimeAppDefinitionRepository runtimeAppDefinitionRepository;
	@Autowired
	protected RuntimeService runtimeService;
	@Autowired
	protected TaskService taskService;
	@Autowired
	protected RepositoryService repositoryService;
	@Autowired
	protected RelatedContentRepository relatedContentRepository;

	@SpyBean
	protected EmailTemplateService emailTemplateService;

	@Autowired
	protected TestWorkflowUtils testUtils;

	protected static Map<String, String> userTokens;
	protected ObjectMapper objectMapper;

	protected static String[] requiredGroups;
	protected static UserBean[] requiredUsers;

	protected static String appResurceDir;
	protected static String processDefinitionId;

	// Test objects
	protected Tenant testTenant;
	protected String testTenantIdForActiviti;

	protected User adminUserForPrivateApi;
	protected User nonAdminUserForPrivateApi;

	public static String adminUserForPrivateApiLogin = "admin@activiti.com";
	public static String nonAdminUserForPrivateApiLogin = "nonadminapi@activiti.com";

	public static String passwordForAllUsers = "password";
	public static String groupAdminUsers = "ADMINS";

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		LicenseStatus.setStatus(LicenseStatus.STATUS_VALID);
		objectMapper = new ObjectMapper();

		// Init Tenant
		testTenant = testUtils.getTenant();
		testTenantIdForActiviti = testUtils.getTenantIdForActiviti();

		// Initialize demo users
		initializeUsers();

		// Initialize la App
		if (appResurceDir != null) {
			initApp(appResurceDir);
		}
		
		// mock init
		
		// Email Template in APS (es: ${emailTemplateService.processCustomEmailTemplate(emailTemplateService.findCustomEmailTemplate(1, 'template custom').getId(), execution.getVariables()).getBody()}
		EmailTemplate et = new EmailTemplate();
		et.setId(1L);

		Mockito.doReturn(et).when(emailTemplateService).findCustomEmailTemplate(Mockito.anyLong(), Mockito.anyString());
		
		ProcessedEmailTemplate pet = new ProcessedEmailTemplate("test", "body test");
		try {
			Mockito.doReturn(pet).when(emailTemplateService).processCustomEmailTemplate(Mockito.anyLong(), Mockito.anyMap());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void cleanUp() throws Exception {

		// wait to avoid conflicts among different background jobs
		testUtils.waitForJobExecutorToProcessAllJobs(5000);

		// remove existent deployments
		List<Deployment> deploymentList = repositoryService.createDeploymentQuery().list();
		for (Deployment deployment : deploymentList) {
			repositoryService.deleteDeployment(deployment.getId(), true);
		}

		// reset database
		runtimeAppRepository.deleteAllInBatch();
		runtimeAppDefinitionRepository.deleteAllInBatch();

		// remove attachments
		relatedContentRepository.deleteAllInBatch();

		// database clean
		tenantEventRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		groupRepository.deleteAllInBatch();
		groupCapabilityRepository.deleteAllInBatch();
	}

	protected void initializeUsers() {

		adminUserForPrivateApi = userService.findActiveUserByEmailFetchGroups(adminUserForPrivateApiLogin);
		if (adminUserForPrivateApi == null) {

			appContext.getBean(TransactionTemplate.class).execute(new TransactionCallback<Void>() {

				@Override
				public Void doInTransaction(TransactionStatus status) {
					// Create admin user in tenant
					adminUserForPrivateApi = userService.createNewUser(adminUserForPrivateApiLogin, "Piergiorgio",
							"Lucidi", passwordForAllUsers, null, UserStatus.ACTIVE, AccountType.ENTERPRISE,
							testTenant.getId());
					userRepository.save(adminUserForPrivateApi);
					addTokenForUser(adminUserForPrivateApi);

					// Create non admin user in tenant
					nonAdminUserForPrivateApi = userService.createNewUser(nonAdminUserForPrivateApiLogin, "Normal",
							"User", passwordForAllUsers, null, UserStatus.ACTIVE, AccountType.ENTERPRISE,
							testTenant.getId());
					userRepository.save(nonAdminUserForPrivateApi);
					addTokenForUser(nonAdminUserForPrivateApi);

					// Make user a tenant admin (this is needed to access REST api)
					Group group = new Group();
					group.setTenantId(testTenant.getId());
					group.setName(groupAdminUsers);
					group.setType(Group.TYPE_SYSTEM_GROUP);
					group.setUsers(new HashSet<User>(1));

					GroupCapability groupCapability = new GroupCapability();
					groupCapability.setName(Capabilities.TENANT_ADMIN);
					groupCapabilityRepository.save(groupCapability);

					Set<GroupCapability> capabilities = new HashSet<GroupCapability>();
					capabilities.add(groupCapability);
					group.setCapabilities(capabilities);

					group.getUsers().add(adminUserForPrivateApi);
					groupService.save(group);

					// create a group with ACCESS_EDITOR capabilities (this is needed to import &
					// deploy apps for non admin users)
					Group kickStartGroup = new Group();
					kickStartGroup.setTenantId(testTenant.getId());
					kickStartGroup.setName("kickStartGroup");
					kickStartGroup.setType(Group.TYPE_SYSTEM_GROUP);
					kickStartGroup.setUsers(new HashSet<User>(1));

					GroupCapability kickStartGroupCapability = new GroupCapability();
					kickStartGroupCapability.setName(Capabilities.ACCESS_EDITOR);
					groupCapabilityRepository.save(kickStartGroupCapability);

					Set<GroupCapability> kickStartGroupCapabilities = new HashSet<GroupCapability>();
					kickStartGroupCapabilities.add(kickStartGroupCapability);
					kickStartGroup.setCapabilities(kickStartGroupCapabilities);

					kickStartGroup.getUsers().add(nonAdminUserForPrivateApi);
					groupService.save(kickStartGroup);

					// groups creation
					if (requiredGroups != null) {
						for (int i = 0; i < requiredGroups.length; i++) {
							Group requiredGroup = new Group();
							requiredGroup.setName(requiredGroups[i]);
							requiredGroup.setTenantId(testTenant.getId());
							requiredGroup.setType(Group.TYPE_FUNCTIONAL_GROUP);

							groupService.save(requiredGroup);
						}
					}

					// users creation
					if (requiredUsers != null) {
						for (int i = 0; i < requiredUsers.length; i++) {

							User requiredUser = userService.createNewUser(requiredUsers[i].getEmail(),
									requiredUsers[i].getName(), requiredUsers[i].getSurname(),
									requiredUsers[i].getPassword(), null, UserStatus.ACTIVE, AccountType.ENTERPRISE,
									testTenant.getId());

							// se ho specificato un externalId lo setto prima del save
							if (requiredUsers[i].getExternalId() != null) {
								requiredUser.setExternalId(requiredUsers[i].getExternalId());
							}

							userRepository.save(requiredUser);

							addTokenForUser(requiredUser);

							for (int j = 0; j < requiredUsers[i].getMemberOf().length; j++) {
								List<Group> groupFounds = groupService.getGroupByNameAndTenantId(
										requiredUsers[i].getMemberOf()[j], testTenant.getId());

								if (groupFounds != null && groupFounds.size() > 0) {
									groupService.addUserToGroup(groupFounds.get(0), requiredUser);
								}

							}
						}
					}

					return null;
				}

			});

			adminUserForPrivateApi = userService.findActiveUserByEmailFetchGroups(adminUserForPrivateApiLogin);
			nonAdminUserForPrivateApi = userService.findActiveUserByEmailFetchGroups(nonAdminUserForPrivateApiLogin);
		}
	}

	/**
	 * Token for authenticating requests (es: getPrivateApiUrlPrefix())
	 * 
	 * @param user
	 */
	private void addTokenForUser(User user) {
		if (userTokens == null) {
			userTokens = new HashMap<String, String>();
		}
		userTokens.put(user.getEmail(), encodeCookieValue(rememberMeService.createAndInsertPersistentToken(user.getId(),
				user.getTenantId(), "127.0.0.1", "test")));
	}

	/**
	 * Encoding Base64
	 * 
	 * @param token
	 * @return
	 */
	private String encodeCookieValue(PersistentToken token) {
		String[] cookieTokens = new String[] { token.getSeries(), token.getTokenValue() };

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cookieTokens.length; i++) {
			sb.append(cookieTokens[i]);

			if (i < cookieTokens.length - 1) {
				sb.append(":");
			}
		}

		String value = sb.toString();

		sb = new StringBuilder(new String(Base64.encode(value.getBytes())));

		while (sb.charAt(sb.length() - 1) == '=') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	/**
	 * 
	 * @param url
	 * @param user
	 * @param expectedStatusCode
	 * @return
	 */
	public ResponseEntity<String> executeGETRequest(String url, User user, HttpStatus expectedStatusCode) {

		ResponseEntity<String> response;

		if (user == null) {
			response = new TestRestTemplate().getForEntity(url, String.class);

		} else if (url.startsWith(getEnterpriseApiUrlPrefix())) {
			TestRestTemplate testRestTemplate = new TestRestTemplate(user.getEmail(), passwordForAllUsers);
			response = testRestTemplate.getForEntity(url, String.class);

		} else {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("Cookie",
					CustomPersistentRememberMeServices.COOKIE_NAME + "=" + userTokens.get(user.getEmail()));
			HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);

			TestRestTemplate testRestTemplate = new TestRestTemplate();
			response = testRestTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		}

		Assert.assertEquals(expectedStatusCode, response.getStatusCode());

		return response;
	}

	/**
	 * @param url
	 * @param boby
	 * @param user
	 * @param expectedStatusCode
	 * @return
	 */
	public ResponseEntity<String> executePUTRequest(String url, Object boby, User user, HttpStatus expectedStatusCode) {
		ResponseEntity<String> response;
		if (user == null) {
			response = (new TestRestTemplate()).exchange(url, HttpMethod.PUT, new HttpEntity<Object>(boby),
					String.class);

		} else if (url.startsWith(getEnterpriseApiUrlPrefix())) {
			response = (new TestRestTemplate(user.getEmail(), passwordForAllUsers)).exchange(url, HttpMethod.PUT,
					new HttpEntity<Object>(boby), String.class);

		} else {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.add("Cookie",
					CustomPersistentRememberMeServices.COOKIE_NAME + "=" + userTokens.get(user.getEmail()));
			HttpEntity<Object> requestEntity = new HttpEntity<Object>(boby, requestHeaders);

			TestRestTemplate testRestTemplate = new TestRestTemplate();
			response = testRestTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

		}

		Assert.assertEquals(expectedStatusCode, response.getStatusCode());

		return response;
	}

	/**
	 * Classloader path for importing, publishing and deploying apps
	 * 
	 * @param appResurceFolder
	 * @return
	 * @throws Exception
	 */
	protected void initApp(String appResurceFolder) throws Exception {
		Long appId = importApp(appResurceFolder);
		assertNotNull(appId);
		assertNotEquals(0L, appId.longValue());

		pubblishApp(appId);
		pubblishApp(appId);

		deployApp(appId);
	}

	/**
	 * @param appResurceFolder
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	protected Long importApp(String appResurceFolder) throws JsonProcessingException, IOException {

		String proposedTempBundleName = "target/app-bundle-integrationtest.zip";

		// Creating ZIP with all the assets
		PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
		try (FileOutputStream fos = new FileOutputStream(proposedTempBundleName);
				ZipOutputStream zos = new ZipOutputStream(fos)) {

			Resource[] appFiles = resourceResolver.getResources(appResurceFolder + "/*.*");
			for (int i = 0; i < appFiles.length; i++) {
				zos.putNextEntry(new ZipEntry(appFiles[i].getFilename()));
				IOUtils.copy(appFiles[i].getInputStream(), zos);
				zos.closeEntry();
			}

			Resource[] bpmnFiles = resourceResolver.getResources(appResurceFolder + "/bpmn-models/*.*");
			for (int i = 0; i < bpmnFiles.length; i++) {
				zos.putNextEntry(new ZipEntry("bpmn-models/" + bpmnFiles[i].getFilename()));
				IOUtils.copy(bpmnFiles[i].getInputStream(), zos);
				zos.closeEntry();
			}

			Resource[] formFiles = resourceResolver.getResources(appResurceFolder + "/form-models/*.*");
			for (int i = 0; i < formFiles.length; i++) {
				zos.putNextEntry(new ZipEntry("form-models/" + formFiles[i].getFilename()));
				IOUtils.copy(formFiles[i].getInputStream(), zos);
				zos.closeEntry();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// recupero lo zip con AppAssets
		FileSystemResource bundleZipFile = new FileSystemResource(proposedTempBundleName);
		assertNotNull(bundleZipFile);

		// Preparo il client HTTP con la giusta auth
		TestRestTemplate restTemplate = new TestRestTemplate(adminUserForPrivateApi.getEmail(), passwordForAllUsers);

		//
		// Importing app
		//
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", bundleZipFile);

		String importAppUrl = getEnterpriseApiURL("app-definitions/import?renewIdmEntries=true");
		ResponseEntity<String> response = restTemplate.postForEntity(importAppUrl, new HttpEntity<>(body, headers),
				String.class);

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

		// get the App Id
		JsonNode importResponseObj = objectMapper.readTree(response.getBody());
		log.info("import App Result: " + importResponseObj);
		assertNotNull(importResponseObj);

		Long appId = importResponseObj.get("id").asLong();

		return appId;
	}

	/**
	 * @param appId
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	protected void pubblishApp(Long appId) throws JsonProcessingException, IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		ResponseEntity<String> response;

		TestRestTemplate restTemplate = new TestRestTemplate(adminUserForPrivateApi.getEmail(), passwordForAllUsers);
		headers.setContentType(MediaType.APPLICATION_JSON);

		ObjectNode jsonBody = objectMapper.createObjectNode();
		jsonBody.put("comment", "");
		jsonBody.put("force", "false");

		String pubblishAppUrl = getEnterpriseApiURL("app-definitions/" + appId + "/publish");
		response = restTemplate.postForEntity(pubblishAppUrl, new HttpEntity<>(jsonBody.toString(), headers),
				String.class);

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

		JsonNode pubblishResponseObj = objectMapper.readTree(response.getBody());
		log.info("pubblish App Result: " + pubblishResponseObj);

		assertFalse(pubblishResponseObj.get("error").asBoolean());
	}

	/**
	 * @param appId
	 */
	protected void deployApp(Long appId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<String> response;
		TestRestTemplate restTemplate = new TestRestTemplate(adminUserForPrivateApi.getEmail(), passwordForAllUsers);

		//
		// deploying app
		//
		String deployJsonObj = "{\"appDefinitions\":[{\"id\":" + appId + "}]}";
		String deplyAppUrl = getEnterpriseApiURL("runtime-app-definitions");
		response = restTemplate.postForEntity(deplyAppUrl, new HttpEntity<>(deployJsonObj, headers), String.class);

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	protected String getServerBaseURL() {
		return "http://localhost:" + httpServerPort + httpServerContextPath;
	}

	protected String getServerApiUrlPrefix() {
		return getServerBaseURL() + "/api/";
	}

	protected String getServerAppUrlPrefix() {
		return getServerBaseURL() + "/app/";
	}

	protected String getEnterpriseApiUrlPrefix() {
		return getServerApiUrlPrefix() + "enterprise/";
	}

	protected String getPrivateApiUrlPrefix() {
		return getServerAppUrlPrefix() + "rest/";
	}

	protected String getEnterpriseApiURL(String apiEndpoint) {
		return getEnterpriseApiUrlPrefix() + apiEndpoint;
	}

	protected String getPrivateApiURL(String apiEndpoint) {
		return getPrivateApiUrlPrefix() + apiEndpoint;
	}
}
