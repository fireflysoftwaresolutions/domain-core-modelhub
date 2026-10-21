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
import reactor.core.publisher.Mono;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public final class SandboxRecordAdapter implements RecordPort {
    static final String ENTITY = "party";
    private final ConcurrentHashMap<UUID, VirtualEntityRecordDto> rows = new ConcurrentHashMap<>();
    @Override public Mono<ModelRecordView> read(UUID id) {
        if (id != null && rows.containsKey(id)) return Mono.just(new ModelRecordView(id, ENTITY));
        UUID resolved = id == null ? UUID.fromString("00000000-0000-4000-8000-000000000001") : id;
        return Mono.just(new ModelRecordView(resolved, ENTITY));
    }
    @Override public Mono<StoredRecord> load(UUID id) {
        VirtualEntityRecordDto body = id == null ? null : rows.get(id);
        if (body == null) return Mono.error(new IllegalStateException("Record " + id + " was not found"));
        return Mono.just(new StoredRecord(id, body));
    }
    @Override public Mono<StoredRecord> create(VirtualEntityRecordDto draft) {
        if (draft == null) return Mono.error(new IllegalArgumentException("Record is required"));
        UUID id = draft.getId() == null ? UUID.randomUUID() : draft.getId();
        draft.setId(id);
        rows.put(id, draft);
        return Mono.just(new StoredRecord(id, draft));
    }
    @Override public Mono<StoredRecord> update(UUID id, VirtualEntityRecordDto draft) {
        if (id == null || !rows.containsKey(id)) return Mono.error(new IllegalStateException("Record " + id + " was not found"));
        if (draft == null) return Mono.error(new IllegalArgumentException("Record is required"));
        draft.setId(id);
        rows.put(id, draft);
        return Mono.just(new StoredRecord(id, draft));
    }
    @Override public Mono<Void> remove(UUID id) {
        if (id != null) rows.remove(id);
        return Mono.empty();
    }
}
