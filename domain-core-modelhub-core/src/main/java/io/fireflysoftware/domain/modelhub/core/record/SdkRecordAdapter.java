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
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.UUID;
public final class SdkRecordAdapter implements RecordPort {
    static final String ENTITY = "party";
    private final DynamicEntityApisApi api;
    public SdkRecordAdapter(DynamicEntityApisApi api) { this.api = api; }
    @Override public Mono<ModelRecordView> read(UUID id) {
        return api.getRecordByIdForEntity(ENTITY, id, null).map(dto -> new ModelRecordView(dto.getId(), ENTITY));
    }
    @Override public Mono<StoredRecord> load(UUID id) {
        return api.getRecordByIdForEntity(ENTITY, id, null).map(dto -> new StoredRecord(dto.getId() == null ? id : dto.getId(), dto));
    }
    @Override public Mono<StoredRecord> create(VirtualEntityRecordDto draft) {
        return api.createRecordForEntity(ENTITY, draft, key()).map(dto -> new StoredRecord(dto.getId(), dto));
    }
    @Override public Mono<StoredRecord> update(UUID id, VirtualEntityRecordDto draft) {
        Map<String, Object> payload = draft == null ? null : draft.getPayload();
        if (payload == null) return Mono.error(new IllegalArgumentException("Record payload is required"));
        return api.updateRecordForEntity(ENTITY, id, payload, key()).map(dto -> new StoredRecord(dto.getId() == null ? id : dto.getId(), dto));
    }
    @Override public Mono<Void> remove(UUID id) {
        return api.deleteRecordForEntity(ENTITY, id, key()).then();
    }
    private static String key() { return UUID.randomUUID().toString(); }
}
