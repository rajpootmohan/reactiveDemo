package com.example.reactiveDemo.serviceImpl;

import com.example.reactiveDemo.configuration.ElasticSearchConfiguration;
import com.example.reactiveDemo.service.ElasticSearchService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticSearchConfiguration elasticSearchConfiguration;

    @Override
    public GetResponse getDocumentByIndexAndTypeAndId(String index, String type, String id)
            throws IOException {
        GetRequest getRequest = new GetRequest(index, type, id);
        return restHighLevelClient.get(getRequest);
    }

    @Override
    public SearchResponse search(String indexName, String indexType, Map<String, String> termMap)
            throws IOException {
        return this.search(indexName, indexType, this.makeQueryBuilderMustExactMatch(termMap), false,
                10, 5);
    }

    @Override
    public SearchResponse search(String indexName, String indexType, QueryBuilder queryBuilder,
                                 boolean isScrollable, int limit, int timeout) throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(limit)
                .query(queryBuilder)
                .timeout(new TimeValue(100, TimeUnit.SECONDS));

        SearchRequest searchRequest = new SearchRequest()
                .indices(indexName)
                .types(indexType)
                .source(searchSourceBuilder);

        if (isScrollable) {
            searchRequest.scroll(new TimeValue(100, TimeUnit.SECONDS));
        }
        return restHighLevelClient.search(searchRequest);
    }

    @Override
    public SearchResponse search(String indexName, String indexType, QueryBuilder queryBuilder,
                                 int from, int limit, int timeout, String sessionId) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(limit)
                .query(queryBuilder)
                .timeout(new TimeValue(timeout, TimeUnit.SECONDS));

        SearchRequest searchRequest = new SearchRequest()
                .indices(indexName)
                .types(indexType)
                .preference(sessionId)
                .source(searchSourceBuilder);

        return restHighLevelClient.search(searchRequest);
    }


    @Override
    public SearchResponse sortedSearch(String indexName, String indexType, QueryBuilder queryBuilder,
                                       SortBuilder sortBuilder, int size, boolean isScrollable) throws IOException {
        return this.sortedSearch(indexName, indexType, queryBuilder, sortBuilder,
                size, isScrollable, 5);
    }

    @Override
    public SearchResponse sortedSearch(String indexName, String indexType, QueryBuilder queryBuilder,
                                       SortBuilder sortBuilder, boolean isScrollable) throws IOException {
        return this.sortedSearch(indexName, indexType, queryBuilder, sortBuilder,
                10, isScrollable, 5);
    }

    @Override
    public SearchResponse sortedSearch(String indexName, String indexType, QueryBuilder queryBuilder,
                                       SortBuilder sortBuilder, int size, boolean isScrollable, int timeout) throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(queryBuilder)
                .size(size)
                .sort(sortBuilder)
                .sort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .timeout(new TimeValue(timeout, TimeUnit.SECONDS));

        SearchRequest searchRequest = new SearchRequest()
                .indices(indexName)
                .types(indexType)
                .source(searchSourceBuilder);

        if (isScrollable) {
            searchRequest.scroll(new TimeValue(60000));
        }
        return restHighLevelClient.search(searchRequest);
    }

    @Override
    public SearchResponse sortedSearch(String indexName, String indexType, QueryBuilder queryBuilder,
                                       SortBuilder sortBuilder, int from, int limit, int timeout, String sessionId)
            throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(from)
                .size(limit)
                .query(queryBuilder)
                .sort(sortBuilder)
                .sort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .timeout(new TimeValue(timeout, TimeUnit.SECONDS));

        SearchRequest searchRequest = new SearchRequest()
                .indices(indexName)
                .types(indexType)
                .preference(sessionId)
                .source(searchSourceBuilder);

        return restHighLevelClient.search(searchRequest);
    }

    @Override
    public SearchResponse searchWithScrollId(String scrollId) throws IOException {
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId)
                .scroll(new TimeValue(30000));

        return restHighLevelClient.searchScroll(searchScrollRequest);
    }

    @Override
    public void clearScroll(String scrollId) throws IOException {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);

        restHighLevelClient.clearScroll(clearScrollRequest);
    }

    private QueryBuilder makeQueryBuilderMustExactMatch(Map<String, String> termMap) {
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();
        for (Map.Entry<String, String> termEntry : termMap.entrySet()) {
            bqb.must(QueryBuilders.termQuery(termEntry.getKey(), termEntry.getValue()));
        }
        return bqb;
    }

    @Override
    public IndexResponse createDocumentWithIndexAndTypeAndId(
            String indexName, String indexType, String id, String jsonData) throws IOException {
        return restHighLevelClient.index(this.makeCreateDocumentWithIndexAndTypeAndIdRequest(indexName, indexType, id, jsonData));
    }

    @Override
    public UpdateResponse updateDocumentWithIndexAndTypeAndId(
            String indexName, String indexType, String id, String jsonData) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(indexName, indexType, id)
                .doc(jsonData, XContentType.JSON);

        return restHighLevelClient.update(updateRequest);
    }

    @Override
    public DeleteResponse deleteDocumentByIndexAndTypeAndId(String indexName, String indexType,
                                                            String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, indexType, id);
        return restHighLevelClient.delete(deleteRequest);
    }

    @Override
    public Boolean isIndexExists(String index) throws IOException {
        OpenIndexRequest openIndexRequest = new OpenIndexRequest(index);
        return restHighLevelClient.indices().open(openIndexRequest).isAcknowledged();
    }

    @Override
    public IndexRequest makeCreateDocumentWithIndexAndTypeAndIdRequest(String indexName,
                                                                       String indexType, String id, String jsonData) {

        IndexRequest indexRequest = new IndexRequest(indexName, indexType, id);
        indexRequest.source(jsonData, XContentType.JSON);

        return indexRequest;
    }

    @Override
    public UpdateRequest makeUpdateDocumentWithIndexAndTypeAndIdRequestAndMap(String indexName,
                                                                              String indexType, String id, Map map) {

        return new UpdateRequest(indexName, indexType, id).doc(map);
    }

    @Override
    public BulkResponse executeBulkCreateRequest(List<IndexRequest> esCreateRequests)
            throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        for (IndexRequest esCreateRequest : esCreateRequests) {
            bulkRequest.add(esCreateRequest);
        }

        return restHighLevelClient.bulk(bulkRequest);
    }

    @Override
    public BulkResponse executeBulkUpdateRequest(List<UpdateRequest> esUpdateRequests)
            throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        esUpdateRequests.forEach(bulkRequest::add);
        return restHighLevelClient.bulk(bulkRequest);
    }

    @Override
    public CreateIndexResponse createIndex(String indexName, String indexType, String jsonMapping)
            throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", elasticSearchConfiguration.getNumberOfShards())
                .put("index.number_of_replicas", elasticSearchConfiguration.getNumberOfReplicas()));
        createIndexRequest.mapping(indexType,
                jsonMapping, XContentType.JSON);
        return restHighLevelClient.indices().create(createIndexRequest);
    }
}
