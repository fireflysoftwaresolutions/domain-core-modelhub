/*
 * Copyright 2026 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fireflysoftware.domain.modelhub.core.record;
import io.fireflysoftware.core.modelhub.model.VirtualEntityRecordDto;
import io.fireflysoftware.core.modelhub.sdk.api.DynamicEntityApisApi;
import io.fireflysoftware.domain.modelhub.infra.RecordClientFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import reactor.core.publisher.Mono;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
class SdkRecordAdapterTest {
    @Test void sandboxIsTheDefaultAndDoesNotOpenASocket() {
        new ApplicationContextRunner().withUserConfiguration(RecordAdapterConfiguration.class).run(context -> {
            RecordPort port = context.getBean(RecordPort.class);
            assertThat(port).isInstanceOf(SandboxRecordAdapter.class);
            assertThat(port.read(null).block().label()).isEqualTo("party");
        });
    }
    @Test void corePropertySelectsTheSdkAdapter() {
        new ApplicationContextRunner().withUserConfiguration(RecordAdapterConfiguration.class, RecordClientFactory.class)
                .withPropertyValues("firefly.modelhub.record.adapter=core")
                .run(context -> assertThat(context.getBean(RecordPort.class)).isInstanceOf(SdkRecordAdapter.class));
    }
    @Test void liveReadCallsTheCoreSdk() {
        UUID id = UUID.randomUUID();
        DynamicEntityApisApi api = mock(DynamicEntityApisApi.class);
        VirtualEntityRecordDto dto = mock(VirtualEntityRecordDto.class);
        when(dto.getId()).thenReturn(id);
        when(api.getRecordByIdForEntity("party", id, null)).thenReturn(Mono.just(dto));
        assertThat(new SdkRecordAdapter(api).read(id).block().id()).isEqualTo(id);
        verify(api).getRecordByIdForEntity("party", id, null);
    }
}
