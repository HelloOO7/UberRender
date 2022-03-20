#version 400 core
uniform sampler2D Textures[1];
in vec3 FS_Normal;
in vec3 FS_View;
in vec2 FS_Texcoord0;

layout(location = 0) out vec4 gbuf_Position;
layout(location = 1) out vec4 gbuf_Normal;
layout(location = 2) out vec4 gbuf_Albedo;
layout(location = 3) out vec4 gbuf_Specular;
layout(location = 4) out vec4 gbuf_Emission;

void main(void) {
	gbuf_Normal = vec4(FS_Normal, 1.0);
	gbuf_Position = vec4(FS_View, 1.0);
	gbuf_Albedo = texture2D(Textures[0], FS_Texcoord0);
	gbuf_Specular = vec4(0.0);
	gbuf_Emission = vec4(0.0);
}