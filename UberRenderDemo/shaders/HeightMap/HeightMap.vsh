#version 400 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec3 a_Normal;
layout(location = 2) in vec3 a_Tangent;
layout(location = 3) in vec2 a_Texcoord0;

uniform mat4 UBR_ModelMatrix;
uniform mat4 UBR_ViewMatrix;
uniform mat4 UBR_ProjectionMatrix;
uniform mat3 UBR_NormalMatrix;

uniform sampler2D HeightMap;

out vec3 FS_Normal;
out vec3 FS_Tangent;
out vec3 FS_View;
out vec2 FS_Texcoord0;

uniform float HeightMapOffset;
uniform float HeightMapScale;

void main(void) {
	vec3 positionAttr = a_Position;
 	vec4 heightMapValue = texture(HeightMap, vec2(a_Texcoord0.x, 1.0 - a_Texcoord0.y));
 	positionAttr.y = (heightMapValue.r) * HeightMapScale + HeightMapOffset;
	vec4 outPosition = UBR_ModelMatrix * vec4(positionAttr, 1.0);
 	FS_View = outPosition.xyz;
 	outPosition = UBR_ProjectionMatrix * UBR_ViewMatrix * outPosition;
	FS_Normal = UBR_NormalMatrix * a_Normal;
	FS_Texcoord0 = a_Texcoord0;
	gl_Position = outPosition;
}