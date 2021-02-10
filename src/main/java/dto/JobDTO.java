package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;

@Data
@AllArgsConstructor
public class JobDTO {
    private String url;
    private Path packagePath;
}
