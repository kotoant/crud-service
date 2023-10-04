package io.github.kotoant.crud.openapi;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;
import io.github.kotoant.crud.rest.api.CrudApi;

@RestController
@Profile("openapi-test")
public class CrudApiStub implements CrudApi {

}
