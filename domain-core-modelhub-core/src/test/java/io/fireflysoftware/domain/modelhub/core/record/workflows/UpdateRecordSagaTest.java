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
import io.fireflysoftware.domain.modelhub.core.record.commands.UpdateRecordCommand;
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
class UpdateRecordSagaTest {
    @Mock private CommandBus commandBus;
    @Test void restoreWritesThePreviousBody() {
        UUID id = UUID.randomUUID();
        VirtualEntityRecordDto previous = new VirtualEntityRecordDto();
        when(commandBus.send(any(UpdateRecordCommand.class))).thenReturn(Mono.empty());
        ExecutionContext ctx = ExecutionContext.forSaga(UpdateRecordSaga.NAME, "test");
        ctx.putVariable("previous", new StoredRecord(id, previous));
        StepVerifier.create(new UpdateRecordSaga(commandBus).restore(new StoredRecord(id, new VirtualEntityRecordDto()), ctx)).verifyComplete();
        ArgumentCaptor<UpdateRecordCommand> captor = ArgumentCaptor.forClass(UpdateRecordCommand.class);
        verify(commandBus).send(captor.capture());
        assertThat(captor.getValue().draft()).isSameAs(previous);
    }
}
