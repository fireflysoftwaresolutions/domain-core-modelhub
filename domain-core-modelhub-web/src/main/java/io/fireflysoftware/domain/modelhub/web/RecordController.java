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

package io.fireflysoftware.domain.modelhub.web;
import io.fireflysoftware.core.modelhub.model.VirtualEntityRecordDto;
import io.fireflysoftware.domain.modelhub.core.record.ModelRecordView;
import io.fireflysoftware.domain.modelhub.core.record.RecordService;
import io.fireflysoftware.domain.modelhub.core.record.StoredRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/records")
public class RecordController {
    private final RecordService service;
    public RecordController(RecordService service) { this.service = service; }
    @GetMapping("/{id}") public Mono<ModelRecordView> read(@PathVariable UUID id) { return service.read(id); }
    @PostMapping public Mono<StoredRecord> create(@RequestBody VirtualEntityRecordDto draft) { return service.register(draft); }
    @PutMapping("/{id}") public Mono<StoredRecord> update(@PathVariable UUID id, @RequestBody VirtualEntityRecordDto draft) { return service.update(id, draft); }
}
