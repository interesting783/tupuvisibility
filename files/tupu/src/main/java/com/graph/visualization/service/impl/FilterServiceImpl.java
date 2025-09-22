package com.graph.visualization.service.impl;

import com.graph.visualization.dto.FilterCondition;
import com.graph.visualization.dto.FilterRequest;
import com.graph.visualization.entity.Node;
import com.graph.visualization.entity.Relationship;
import com.graph.visualization.enums.FilterOperatorEnum;
import com.graph.visualization.enums.LogicalOperatorEnum;
import com.graph.visualization.enums.PropertyTypeEnum;
import com.graph.visualization.repository.NodeRepository;
import com.graph.visualization.repository.RelationshipRepository;
import com.graph.visualization.service.FilterService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤服务实现类
 */
@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {
    
    private final NodeRepository nodeRepository;
    private final RelationshipRepository relationshipRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Node> filterNodes(FilterRequest filterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Node> query = cb.createQuery(Node.class);
        Root<Node> root = query.from(Node.class);
        
        // 根据过滤条件构建谓词
        Predicate predicate = buildPredicate(filterRequest, cb, root, Node.class);
        query.where(predicate);
        
        return entityManager.createQuery(query).getResultList();
    }
    
    @Override
    public List<Relationship> filterRelationships(FilterRequest filterRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Relationship> query = cb.createQuery(Relationship.class);
        Root<Relationship> root = query.from(Relationship.class);
        
        // 根据过滤条件构建谓词
        Predicate predicate = buildPredicate(filterRequest, cb, root, Relationship.class);
        query.where(predicate);
        
        return entityManager.createQuery(query).getResultList();
    }
    
    /**
     * 构建过滤谓词
     */
    private <T> Predicate buildPredicate(FilterRequest filterRequest, CriteriaBuilder cb, Root<T> root, Class<T> entityClass) {
        List<Predicate> predicates = new ArrayList<>();
        
        for (FilterCondition condition : filterRequest.getConditions()) {
            String propertyId = condition.getPropertyId();
            String value = condition.getValue();
            FilterOperatorEnum operator = condition.getOperator();
            PropertyTypeEnum valueType = condition.getValueType();
            
            Path<String> path = root.get(propertyId);
            Predicate singlePredicate = null;
            
            // 根据操作符类型构建谓词
            if (operator == FilterOperatorEnum.EQUALS) {
                singlePredicate = cb.equal(path, value);
            } else if (operator == FilterOperatorEnum.CONTAINS) {
                singlePredicate = cb.like(path, "%" + value + "%");
            } else if (operator == FilterOperatorEnum.STARTS_WITH) {
                singlePredicate = cb.like(path, value + "%");
            } else if (operator == FilterOperatorEnum.ENDS_WITH) {
                singlePredicate = cb.like(path, "%" + value);
            } else if (operator == FilterOperatorEnum.GT) {
                if (valueType == PropertyTypeEnum.DATA) {
                    try {
                        Double numValue = Double.parseDouble(value);
                        singlePredicate = cb.gt(root.get(propertyId), numValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("无法将值转换为数字: " + value);
                    }
                }
            } else if (operator == FilterOperatorEnum.LT) {
                if (valueType == PropertyTypeEnum.DATA) {
                    try {
                        Double numValue = Double.parseDouble(value);
                        singlePredicate = cb.lt(root.get(propertyId), numValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("无法将值转换为数字: " + value);
                    }
                }
            } else if (operator == FilterOperatorEnum.GTE) {
                if (valueType == PropertyTypeEnum.DATA) {
                    try {
                        Double numValue = Double.parseDouble(value);
                        singlePredicate = cb.ge(root.get(propertyId), numValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("无法将值转换为数字: " + value);
                    }
                }
            } else if (operator == FilterOperatorEnum.LTE) {
                if (valueType == PropertyTypeEnum.DATA) {
                    try {
                        Double numValue = Double.parseDouble(value);
                        singlePredicate = cb.le(root.get(propertyId), numValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("无法将值转换为数字: " + value);
                    }
                }
            }
            
            if (singlePredicate != null) {
                predicates.add(singlePredicate);
            }
        }
        
        // 根据逻辑操作符组合谓词
        if (predicates.isEmpty()) {
            return cb.conjunction(); // 返回恒真谓词
        } else if (filterRequest.getLogicalOperator() == LogicalOperatorEnum.AND) {
            return cb.and(predicates.toArray(new Predicate[0]));
        } else {
            return cb.or(predicates.toArray(new Predicate[0]));
        }
    }
}
