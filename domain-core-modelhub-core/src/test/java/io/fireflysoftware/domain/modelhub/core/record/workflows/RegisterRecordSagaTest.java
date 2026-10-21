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

package io.fireflysoftware.domain.modelhub.core.record.workflows;
import io.fireflysoftware.core.modelhub.model.VirtualEntityRecordDto;
import io.fireflysoftware.domain.modelhub.core.record.StoredRecord;
import io.fireflysoftware.domain.modelhub.core.record.commands.RegisterRecordCommand;
import io.fireflysoftware.domain.modelhub.core.record.commands.RemoveRecordCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class RegisterRecordSagaTest {
    @Mock private CommandBus commandBus;
    @Test void removeDeletesTheCreatedRecord() {
        UUID id = UUID.randomUUID();
        when(commandBus.send(any(RemoveRecordCommand.class))).thenReturn(Mono.empty());
        StepVerifier.create(new RegisterRecordSaga(commandBus).remove(new StoredRecord(id, new VirtualEntityRecordDto()))).verifyComplete();
        ArgumentCaptor<RemoveRecordCommand> captor = ArgumentCaptor.forClass(RemoveRecordCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
    }
    @Test void registerStoresTheCoreResult() {
        UUID id = UUID.randomUUID();
        StoredRecord created = new StoredRecord(id, new VirtualEntityRecordDto());
        when(commandBus.send(any(RegisterRecordCommand.class))).thenReturn(Mono.just(created));
        ExecutionContext ctx = ExecutionContext.forSaga(RegisterRecordSaga.NAME, "test");
        StepVerifier.create(new RegisterRecordSaga(commandBus).register(new RegisterRecordCommand(new VirtualEntityRecordDto()), ctx))
                .expectNext(created).verifyComplete();
    }
}
