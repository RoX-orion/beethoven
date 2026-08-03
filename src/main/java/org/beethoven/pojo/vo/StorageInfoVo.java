package org.beethoven.pojo.vo;

import lombok.Data;
import org.beethoven.pojo.enums.StorageProvider;

/**
 * Storage configuration exposed to clients. Credentials are intentionally omitted.
 */
@Data
public class StorageInfoVo {

    private StorageProvider provider;

    private String bucket;

    private String endpoint;
}
