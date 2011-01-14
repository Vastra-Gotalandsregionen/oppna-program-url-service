package se.vgregion.urlservice.metadata;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import se.vgr.metaservice.schema.node.v2.NodeType;
import se.vgr.metaservice.schema.response.v1.NodeListResponseObjectType;
import se.vgregion.metaservice.vocabularyservice.intsvc.VocabularyServiceIntServiceImplService;
import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.Keyword.KeywordClass;
import vocabularyservices.wsdl.metaservice_vgr_se.v2.GetVocabularyRequest;
import vocabularyservices.wsdl.metaservice_vgr_se.v2.VocabularyService;

public class KeywordSyncronizer {
    
    private final Logger logger = LoggerFactory.getLogger(KeywordSyncronizer.class); 
    
    private static final Map<KeywordClass, String> PATHS = new LinkedHashMap<Keyword.KeywordClass, String>();
    static {
        PATHS.put(KeywordClass.WHITE, "VGR/UserGeneratedKeywords/Whitelist");
        PATHS.put(KeywordClass.GREEN, "VGR/UserGeneratedKeywords/Greenlist");
        PATHS.put(KeywordClass.GREY, "VGR/UserGeneratedKeywords/Reviewlist");
        PATHS.put(KeywordClass.BLACK, "VGR/UserGeneratedKeywords/Blacklist");
    }
    
    private KeywordRepository keywordRepository;

    private VocabularyService vocabularyService;
    
    private TransactionTemplate transactionTemplate;

    public KeywordSyncronizer(KeywordRepository keywordRepository, VocabularyService vocabularyService, TransactionTemplate transactionTemplate) {
        Assert.notNull(keywordRepository, "keywordRepository can not be null");
        Assert.notNull(keywordRepository, "vocabularyService can not be null");
        Assert.notNull(transactionTemplate, "transactionTemplate can not be null");
        
        this.keywordRepository = keywordRepository;
        this.vocabularyService = vocabularyService;
        this.transactionTemplate = transactionTemplate;
    }

    @Autowired
    public KeywordSyncronizer(KeywordRepository keywordRepository, TransactionTemplate transactionTemplate) {
        this.keywordRepository = keywordRepository;
        URL wsdlURL = KeywordSyncronizer.class.getClassLoader().getResource("wsdl/VocabularyService.wsdl");

        VocabularyServiceIntServiceImplService ss = new VocabularyServiceIntServiceImplService(wsdlURL);
        this.vocabularyService = ss.getPort(VocabularyService.class);
        this.transactionTemplate = transactionTemplate;
    }

    public void run() {
        logger.info("Starting keyword syncronization");
        
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                
                for (Entry<KeywordClass, String> path : PATHS.entrySet()) {
                    GetVocabularyRequest request = new GetVocabularyRequest();
                    request.setPath(path.getValue());
                    
                    KeywordClass classification = path.getKey();
                    
                    logger.debug("Retriving keyword metadata for classification {}", classification);
                    NodeListResponseObjectType response = vocabularyService.getVocabulary(request);
                    
                    
                    if (response.getStatusCode() == 200) {
                        logger.debug("Successfully retrived keyword metadata for classification {}", classification);
                        for (NodeType node : response.getNodeList().getNode()) {
                            String name = node.getName();
                            
                            Keyword keyword = keywordRepository.findByName(name);
                            if (keyword != null) {
                                keyword.setClassification(classification);
                            } else {
                                // create new keyword
                                keyword = new Keyword(name, classification);
                                keywordRepository.persist(keyword);
                            }
                            
                        }
                    } else {
                        logger.warn("Successfully retrived keyword metadata for classification {}", classification);
                        System.err.println(response.getErrorMessage());
                    }
                }
                
            }
        });
        
    }
}
