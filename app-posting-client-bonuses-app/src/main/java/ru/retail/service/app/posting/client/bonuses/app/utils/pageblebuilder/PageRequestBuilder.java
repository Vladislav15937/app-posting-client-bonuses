package ru.retail.service.app.posting.client.bonuses.app.utils.pageblebuilder;

import org.springframework.data.domain.Pageable;

public interface PageRequestBuilder {

    Pageable createPageRequest(int page, int size, String sort);
}
