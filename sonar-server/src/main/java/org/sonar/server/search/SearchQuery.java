/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.FacetBuilders;

import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.FilterBuilders.matchAllFilter;
import static org.elasticsearch.index.query.FilterBuilders.queryFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;

/**
 * This class can be used to build "AND" form queries, eventually with query facets, to be passed to {@link SearchIndex#findHits(SearchQuery)}
 * For instance the following code:
 * <blockquote>
   SearchQuery.create("polop")
      .field("field1", "value1")
      .field("field2", "value2")
   </blockquote>
 * ...yields the following query string:<br/>
 * <blockquote>
   polop AND field1:value1 AND field2:value2
   </blockquote>
 * @since 4.1
 */
public class SearchQuery {
  private int scrollSize;
  private String searchString;
  private List<String> indices;
  private List<String> types;
  private Map<String, String> fieldCriteria;
  private Map<String, String> termFacets;

  private SearchQuery() {
    scrollSize = 10;
    indices = Lists.newArrayList();
    types = Lists.newArrayList();
    fieldCriteria = Maps.newLinkedHashMap();
    termFacets = Maps.newHashMap();
  }

  public static SearchQuery create() {
    return new SearchQuery();
  }

  public SearchQuery searchString(String searchString) {
    this.searchString = searchString;
    return this;
  }

  public SearchQuery scrollSize(int scrollSize) {
    this.scrollSize = scrollSize;
    return this;
  }

  int scrollSize() {
    return scrollSize;
  }

  public SearchQuery index(String index) {
    indices.add(index);
    return this;
  }

  public SearchQuery type(String type) {
    types.add(type);
    return this;
  }

  public SearchQuery field(String fieldName, String fieldValue) {
    fieldCriteria.put(fieldName, fieldValue);
    return this;
  }

  public SearchQuery facet(String facetName, String fieldName) {
    fieldCriteria.put(facetName, fieldName);
    return this;
  }

  private SearchRequestBuilder addFacets(SearchRequestBuilder builder) {
    for (String facetName: termFacets.keySet()) {
      builder.addFacet(FacetBuilders.termsFacet(facetName).field(termFacets.get(facetName)));
    }
    return builder;
  }

  SearchRequestBuilder toBuilder(Client client) {
    SearchRequestBuilder builder = client.prepareSearch(indices.toArray(new String[0])).setTypes(types.toArray(new String[0]));
    List<FilterBuilder> filters = Lists.newArrayList();
    if (StringUtils.isNotBlank(searchString)) {
      filters.add(queryFilter(QueryBuilders.queryString(searchString)));
    }
    for (String field: fieldCriteria.keySet()) {
      filters.add(termFilter(field, fieldCriteria.get(field)));
    }

    if (filters.isEmpty()) {
      builder.setFilter(matchAllFilter());
    } else if(filters.size() == 1) {
      builder.setFilter(filters.get(0));
    } else {
      builder.setFilter(FilterBuilders.andFilter(filters.toArray(new FilterBuilder[0])));
    }
    return addFacets(builder);
  }

  public String getQueryString() {
    return "";
  }
}
