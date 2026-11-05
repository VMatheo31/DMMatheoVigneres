package m2fst.matheo.dm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImagePutDto {

    private String url;

    public static ImagePutDto fromEntity(String url) {
        ImagePutDto rep = new ImagePutDto();
        rep.setUrl(url);
        return rep;
    }
}

