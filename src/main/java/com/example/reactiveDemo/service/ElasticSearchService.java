package com.example.reactiveDemo.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

public interface ElasticSearchService {

    GetResponse getDocumentByIndexAndTypeAndId(String index, String type, String id)
            throws IOException;

    SearchResponse search(String indexName, String indexType, Map<String, String> termMap)
            throws IOException;

    SearchResponse search(String indexName, String indexType, QueryBuilder queryBuilder,
                          boolean isScrollable, int limit, int timeout) throws IOException;

    SearchResponse search(String indexName, String indexType, QueryBuilder queryBuilder,
                          int from, int limit, int timeout, String sessionId) throws IOException;

    SearchResponse sortedSearch(String indexName, String indexType, QueryBuilder queryBuilder,
                                SortBuilder sortBuilder, boolean isScrollable) throws IOException;

    SearchResponse sortedSearch(String indexName, String indexType, QueryBuilder queryBuilder,
                                SortBuilder sortBuilder, int size, boolean isScrollable) throws IOException;

    SearchResponse sortedSearch(String indexName, String indexType, QueryBuilder queryBuilder,
                                SortBuilder sortBuilder, int size, boolean isScrollable, int timeout) throws IOException;

    SearchResponse sortedSearch(String indexName, String indexType, QueryBuilder queryBuilder,
                                SortBuilder sortBuilder, int from, int limit, int timeout, String sessionId)
            throws IOException;

    SearchResponse searchWithScrollId(String scrollId) throws IOException;

    void clearScroll(String scrollId) throws IOException;

    IndexResponse createDocumentWithIndexAndTypeAndId(String indexName, String indexType, String id,
                                                      String jsonData)
            throws IOException;

    UpdateResponse updateDocumentWithIndexAndTypeAndId(String indexName, String indexType, String id,
                                                       String jsonData)
            throws IOException;

    DeleteResponse deleteDocumentByIndexAndTypeAndId(String indexName, String indexType, String id)
            throws IOException;

    Boolean isIndexExists(String index) throws IOException;

    IndexRequest makeCreateDocumentWithIndexAndTypeAndIdRequest(String indexName,
                                                                String indexType, String id, String jsonData);

    UpdateRequest makeUpdateDocumentWithIndexAndTypeAndIdRequestAndMap(String indexName,
                                                                       String indexType, String id, Map map);

    BulkResponse executeBulkCreateRequest(List<IndexRequest> esCreateRequests) throws IOException;

    BulkResponse executeBulkUpdateRequest(List<UpdateRequest> esUpdateRequests) throws IOException;

    CreateIndexResponse createIndex(String indexName, String indexType, String jsonMapping)
            throws IOException;
}
