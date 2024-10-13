package org.example.module;

import com.google.inject.AbstractModule;
import org.example.library.storage.FetchBackendProjectStorageManager;

public class FetchBackendProjectModule extends AbstractModule {
    @Override
    protected void configure() throws RuntimeException{
        install(new MysqlClientModule());
        bind(FetchBackendProjectStorageManager.class).toInstance(new FetchBackendProjectStorageManager());
    }
}
