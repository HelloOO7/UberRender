package urender.engine;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;

public class UMaterial {
	
	public final List<UTextureMapper> textures = new ArrayList<>();
	
	public static class UTextureMapper {
		public String textureName;
		public String meshUVSetName;
		
		public String shaderVariableName;
		
		public Vector2f translation;
		public float rotation;
		public Vector2f scale;
	}
}
