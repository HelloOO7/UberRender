#version 400 core

layout(location = 0) in vec3 a_Position;

out vec2 gbuf_Texcoord;

void main(void) {
	vec4 outPosition = vec4(a_Position, 1.0);
	gbuf_Texcoord = (outPosition.xy + 1.0) * 0.5;
	gl_Position = outPosition;
}