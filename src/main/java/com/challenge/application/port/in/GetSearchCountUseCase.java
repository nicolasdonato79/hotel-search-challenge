package com.challenge.application.port.in;

import com.challenge.domain.model.SearchCountResult;

public interface GetSearchCountUseCase {
    SearchCountResult getBySearchId(String searchId);
}