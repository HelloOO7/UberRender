#version 400 core
uniform sampler2D Textures[1];
in vec3 FS_Normal;
in vec3 FS_View;
in vec2 FS_Texcoord0;

out vec4 FragColor;

void main(void) {
	FragColor = texture2D(Textures[0], FS_View.xy) * clamp(dot(FS_Normal, -vec3(0.0, 0.0, -1.0)), 0.0, 1.0);
}