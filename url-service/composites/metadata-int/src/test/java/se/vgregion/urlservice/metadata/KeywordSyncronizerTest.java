package se.vgregion.urlservice.metadata;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.internal.progress.ArgumentMatcherStorage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import se.vgr.metaservice.schema.node.v2.NodeListType;
import se.vgr.metaservice.schema.node.v2.NodeType;
import se.vgr.metaservice.schema.response.v1.NodeListResponseObjectType;
import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.Keyword.KeywordClass;
import vocabularyservices.wsdl.metaservice_vgr_se.v2.GetVocabularyRequest;
import vocabularyservices.wsdl.metaservice_vgr_se.v2.VocabularyService;

public class KeywordSyncronizerTest {

    private KeywordRepository keywordRepository = Mockito.mock(KeywordRepository.class);
    private VocabularyService vocabularyService = Mockito.mock(VocabularyService.class);

    @Test
    public void test() {
        NodeListResponseObjectType nodeListResponse = new NodeListResponseObjectType();
        nodeListResponse.setStatusCode(200);
        NodeListType nodeList = new NodeListType();
        NodeType node1 = new NodeType();
        node1.setName("kw1");
        NodeType node2 = new NodeType();
        node2.setName("kw2");
        nodeList.getNode().add(node1);
        nodeList.getNode().add(node2);
        nodeListResponse.setNodeList(nodeList);
        
        PlatformTransactionManager txManager = Mockito.mock(PlatformTransactionManager.class);
        TransactionTemplate transactionTemplate = new TransactionTemplate(txManager);
        Mockito.when(vocabularyService.getVocabulary(Mockito.any(GetVocabularyRequest.class))).thenReturn(nodeListResponse);
        Mockito.when(keywordRepository.findByName("kw1")).thenReturn(new Keyword("kw1"));
        
        KeywordSyncronizer syncronizer = new KeywordSyncronizer(keywordRepository, vocabularyService, transactionTemplate);
        syncronizer.run();
        
        Mockito.verify(keywordRepository).persist(Mockito.argThat(new KeywordMatcher("kw2", KeywordClass.WHITE)));
        Mockito.verify(keywordRepository).persist(Mockito.argThat(new KeywordMatcher("kw2", KeywordClass.GREEN)));
        Mockito.verify(keywordRepository).persist(Mockito.argThat(new KeywordMatcher("kw2", KeywordClass.GREY)));
        Mockito.verify(keywordRepository).persist(Mockito.argThat(new KeywordMatcher("kw2", KeywordClass.BLACK)));
    }
 
    public static class KeywordMatcher extends ArgumentMatcher<Keyword> {
        private String name;
        private KeywordClass classification;
        
        public KeywordMatcher(String name, KeywordClass classification) {
            this.name = name;
            this.classification = classification;
        }

        @Override
        public boolean matches(Object arg) {
            Keyword that = (Keyword) arg;
            return that.getName().equals(name) && that.getClassification().equals(classification);
        }
        
    }
}
