#version 400 core

uniform sampler2D NormalMap;

in vec3 FS_Normal;
in vec3 FS_Tangent;
in vec3 FS_View;
in vec2 FS_Texcoord0;

out vec4 FragColor;

void main(void) {
	vec3 normal = texture2D(NormalMap, FS_Texcoord0).xyz;
	FragColor = vec4(vec3(clamp(dot(normal, -vec3(0.0, 0.0, -1.0)), 0.0, 1.0)), 1.0);
}