package ru.retail.service.app.posting.client.bonuses.app.utils.pageblebuilder.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.retail.service.app.posting.client.bonuses.app.utils.pageblebuilder.PageRequestBuilder;

import java.util.Set;

@Component
public class PageRequestBuilderForBonusImpl implements PageRequestBuilder {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "amount", "type", "createdAt", "status"
    );

    /**
     * Создание PageRequest с валидацией параметров
     */
    public Pageable createPageRequest(int page, int size, String sort) {
        int validatedSize = Math.min(size, MAX_PAGE_SIZE);
        try {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0];
            Sort.Direction direction = sortParams.length > 1
                    ? Sort.Direction.fromString(sortParams[1])
                    : Sort.Direction.DESC;
            if (!isValidSortField(sortField)) {
                sortField = "createdAt";
                direction = Sort.Direction.DESC;
            }
            return PageRequest.of(page, validatedSize, Sort.by(direction, sortField));
        } catch (Exception e) {
            return PageRequest.of(page, validatedSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
    }

    private boolean isValidSortField(String field) {
        return ALLOWED_SORT_FIELDS.contains(field);
    }
}
