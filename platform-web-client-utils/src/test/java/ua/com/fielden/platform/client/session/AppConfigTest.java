package ua.com.fielden.platform.client.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Test;

import ua.com.fielden.platform.client.session.AppSessionController;

/**
 * Test case to ensure that application properties are store and retrieved correctly.
 *
 * @author TG Team
 *
 */
public class AppConfigTest {
    private final String path = "src/test/resources/file.properties";

    @After
    public void removePropertyFile() {
	final File file = new File(path);
	if (file.exists()) {
	   file.delete();
	}
    }

    @Test
    public void test_saving_to_a_new_file() throws Exception {
	removePropertyFile();
	// save properties
	AppSessionController config = new AppSessionController(path, null);
	assertNull("Property should not yet be set.", config.getUsername());
	assertNull("Property should not yet be set.", config.getPrivateKey());
	config.persist("username", "some private key");
	// load properties and assert
	config = new AppSessionController(path, null);
	assertTrue("Missing username.", !StringUtils.isEmpty(config.getUsername()));
	assertEquals("Incorrect username.", "username", config.getUsername());
	assertTrue("Missing private key.", !StringUtils.isEmpty(config.getPrivateKey()));
	assertEquals("Incorrect private key.", "some private key", config.getPrivateKey());
    }

    @Test
    public void test_chaning_properties() throws Exception {
	removePropertyFile();
	// save properties
	AppSessionController config = new AppSessionController(path, null);
	config.persist("username", "some private key");
	// load properties and assert
	config = new AppSessionController(".file.properties", null);
	config.persist("another username", "some other private key");

	assertEquals("Incorrect username.", "another username", config.getUsername());
	assertEquals("Incorrect private key.", "some other private key", config.getPrivateKey());
    }

}
