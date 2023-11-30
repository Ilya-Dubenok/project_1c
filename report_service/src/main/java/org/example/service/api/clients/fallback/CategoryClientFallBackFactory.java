package org.example.service.api.clients.fallback;

import lombok.extern.slf4j.Slf4j;
import org.example.core.dto.category.CategoryDTO;
import org.example.core.dto.exception.OtherServiceUnavailableException;
import org.example.core.dto.rule.RuleDTO;
import org.example.core.dto.rule.RuleType;
import org.example.service.api.clients.ICategoryClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class CategoryClientFallBackFactory implements FallbackFactory<ICategoryClient> {

    @Override
    public ICategoryClient create(Throwable cause) {
        log.error("[FALLBACK] Could not reach category-service", cause);
        return new ICategoryClient() {
            @Override
            public Boolean categoryExists(UUID categoryId) {
                throw new OtherServiceUnavailableException();
            }

            @Override
            public List<RuleDTO> getRulesApplicableFromCategories(UUID categoryId, Set<RuleType> ruleTypeSet) {
                throw new OtherServiceUnavailableException();
            }

            @Override
            public List<CategoryDTO> getCategoryAndParents(UUID categoryId) {
                throw new OtherServiceUnavailableException();
            }
        };
    }
}
