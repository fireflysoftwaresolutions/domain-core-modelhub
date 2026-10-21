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
import io.fireflysoftware.domain.modelhub.core.record.commands.LoadRecordCommand;
import io.fireflysoftware.domain.modelhub.core.record.commands.RegisterRecordCommand;
import io.fireflysoftware.domain.modelhub.core.record.commands.UpdateRecordCommand;
import io.fireflysoftware.domain.modelhub.core.record.workflows.RegisterRecordSaga;
import io.fireflysoftware.domain.modelhub.core.record.workflows.UpdateRecordSaga;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.UUID;
@Service
public class RecordService {
    private final SagaEngine engine;
    private final RecordPort port;
    public RecordService(SagaEngine engine, RecordPort port) { this.engine = engine; this.port = port; }
    public Mono<ModelRecordView> read(UUID id) { return port.read(id); }
    public Mono<StoredRecord> register(VirtualEntityRecordDto draft) {
        StepInputs inputs = StepInputs.builder().forStepId(RegisterRecordSaga.STEP, new RegisterRecordCommand(draft)).build();
        return engine.execute(RegisterRecordSaga.NAME, inputs).flatMap(result -> outcome(result, RegisterRecordSaga.STEP));
    }
    public Mono<StoredRecord> update(UUID id, VirtualEntityRecordDto draft) {
        StepInputs inputs = StepInputs.builder()
                .forStepId(UpdateRecordSaga.LOAD, new LoadRecordCommand(id))
                .forStepId(UpdateRecordSaga.STEP, new UpdateRecordCommand(id, draft)).build();
        return engine.execute(UpdateRecordSaga.NAME, inputs).flatMap(result -> outcome(result, UpdateRecordSaga.STEP));
    }
    private static Mono<StoredRecord> outcome(SagaResult result, String stepId) {
        if (result.isFailed()) return Mono.error(result.error().orElseGet(() -> new IllegalStateException("Saga failed")));
        return Mono.justOrEmpty(result.resultOf(stepId, StoredRecord.class))
                .switchIfEmpty(Mono.error(new IllegalStateException("Saga step " + stepId + " produced no result")));
    }
}
