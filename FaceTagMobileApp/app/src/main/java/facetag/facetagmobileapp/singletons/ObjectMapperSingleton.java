package facetag.facetagmobileapp.singletons;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by chris-ubuntu on 22/07/15.
 * Mappers are expansive to initialize, so it's best to just initialize on and reuse it.
 */
public class ObjectMapperSingleton {
    private static ObjectMapper mapper;

    public static ObjectMapper getObjectMapper(){
        if (mapper == null){
            mapper = new ObjectMapper();
        }
        return mapper;
    }
}
