package se.vgregion.urlservice.repository.jpa;

import java.net.URI;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.BookmarkRepository;
import se.vgregion.urlservice.repository.LongUrlRepository;
import se.vgregion.urlservice.repository.UserRepository;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.Owner;

@ContextConfiguration({"classpath:spring/services-common.xml", "classpath:loadtest.xml"})
public class LoadTestingScript extends AbstractTransactionalJUnit4SpringContextTests { 

	@Ignore // remove when running
    @Test
    @Transactional()
    @Rollback(false)
    public void setupLoadTestData() {
    	for(int i = 5; i<1000; i++) {
    		String hash = String.format("%05d", i);
	        LongUrl longUrl = new LongUrl(URI.create("http://example.com/" + hash), hash);
	        
	        // the keyword, owner and LongUrl must be persisted first
	        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
	        
	        Owner owner = userRepository.findByName("test");
	        if(owner == null) {
	        	owner = new Owner("test");
	        	userRepository.persist(owner);
	        	userRepository.flush();
	        }
	        
	        LongUrlRepository longUrlRepository = applicationContext.getBean(LongUrlRepository.class);
	        longUrlRepository.persist(longUrl);
	        longUrlRepository.flush();
	        Bookmark shortLink = new Bookmark(hash, longUrl, Collections.EMPTY_LIST, owner);
	
	        BookmarkRepository bookmarkRepository = applicationContext.getBean(BookmarkRepository.class);
	        
	        bookmarkRepository.persist(shortLink);
	        bookmarkRepository.flush();
    	}
    }

}
