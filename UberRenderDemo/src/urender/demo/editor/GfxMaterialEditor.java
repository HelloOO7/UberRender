/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package urender.demo.editor;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.PlainDocument;
import urender.api.UShaderType;
import urender.engine.UMaterial;
import urender.engine.UTexture;
import urender.engine.UTextureMapper;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniform;
import urender.engine.shader.UUniformFloat;
import urender.engine.shader.UUniformInt;
import urender.engine.shader.UUniformVector2;
import urender.engine.shader.UUniformVector3;
import urender.engine.shader.UUniformVector4;
import urender.g3dio.generic.IIOTextureLoader;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class GfxMaterialEditor extends javax.swing.JFrame {

	//Used for both the JList and the selector comboboxes
	private final SynchronizedListModel<UShader, ShaderEditHandle> shaderListModel = new SynchronizedListModel<UShader, ShaderEditHandle>(new ArrayList()) {
		@Override
		public ShaderEditHandle createHandle(UShader element) {
			return new ShaderEditHandle(element);
		}
	};
	private final SynchronizedListModel<UShaderProgram, ShaderProgramEditHandle> programListModel = new SynchronizedListModel<UShaderProgram, ShaderProgramEditHandle>(new ArrayList<>()) {
		@Override
		public ShaderProgramEditHandle createHandle(UShaderProgram element) {
			return new ShaderProgramEditHandle(element, shaderListModel);
		}
	};
	private final SynchronizedListModel<UTexture, TextureEditHandle> textureListModel = new SynchronizedListModel<UTexture, TextureEditHandle>(new ArrayList<>()) {
		@Override
		public TextureEditHandle createHandle(UTexture element) {
			return new TextureEditHandle(element);
		}
	};
	private final SynchronizedListModel<UMaterial, MaterialEditHandle> materialListModel = new SynchronizedListModel<UMaterial, MaterialEditHandle>(new ArrayList<>()) {
		@Override
		public MaterialEditHandle createHandle(UMaterial element) {
			return new MaterialEditHandle(element, programListModel, textureListModel);
		}
	};
	private final SynchronizedListModel<UUniform, UniformEditHandle> uniformListModel = new SynchronizedListModel<UUniform, UniformEditHandle>(new ArrayList<>()) {
		@Override
		public UniformEditHandle createHandle(UUniform element) {
			return new UniformEditHandle(element);
		}
	};

	/**
	 * Creates new form GfxMaterialEditor
	 */
	public GfxMaterialEditor() {
		initComponents();

		shaderList.setModel(shaderListModel);
		shaderList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				ShaderEditHandle handle = shaderList.getSelectedValue();
				if (handle != null) {
					shaderEditorArea.setDocument(handle.document);
				} else {
					shaderEditorArea.setDocument(new PlainDocument());
				}
			}
		}));

		addRemoveShader.bind(shaderList, shaderListModel, new AddRemoveItemButtons.Handler<UShader>() {
			@Override
			public UShader add() {
				File shaderFile = EditorUIUtility.callFileSelect(GfxMaterialEditor.this, "GLSL Shader | *.vsh, *.fsh", ".vsh", ".fsh");
				if (shaderFile != null) {
					try {
						return new UShader(
							shaderFile.getName(),
							shaderFile.getName().endsWith(".fsh") ? UShaderType.FRAGMENT : UShaderType.VERTEX,
							new String(Files.readAllBytes(shaderFile.toPath()), StandardCharsets.UTF_8)
						);
					} catch (IOException ex) {
						Logger.getLogger(GfxMaterialEditor.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				return null;
			}

			@Override
			public boolean remove(UShader value) {
				String name = value.getName();
				for (ShaderProgramEditHandle pe : programListModel) {
					if (pe.program.getFshName().equals(name) || pe.program.getVshName().equals(name)) {
						EditorUIUtility.showInfoMessage(GfxMaterialEditor.this, "Shader in use", "Please detach this shader from any programs in order to remove it.");
						return false;
					}
				}
				return true;
			}
		});

		shaderProgramList.setModel(programListModel);
		shaderProgramList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				ShaderProgramEditHandle handle = shaderProgramList.getSelectedValue();
				if (handle != null) {
					shaderSelectF.setModel(handle.shaderSelectF);
					shaderSelectV.setModel(handle.shaderSelectV);
				} else {
					shaderSelectF.setModel(new DefaultComboBoxModel<>());
					shaderSelectV.setModel(new DefaultComboBoxModel<>());
				}
			}
		}));

		addRemoveProgram.bind(shaderProgramList, programListModel, new AddRemoveItemButtons.Handler<UShaderProgram>() {
			@Override
			public UShaderProgram add() {
				String name = EditorUIUtility.callNameInput(GfxMaterialEditor.this, "Name input", "Enter a name for the shader program");
				if (name != null) {
					UShaderProgram program = new UShaderProgram(name, null, null);
					return program;
				}
				return null;
			}

			@Override
			public boolean remove(UShaderProgram value) {
				for (MaterialEditHandle mat : materialListModel) {
					if (Objects.equals(mat.material.getShaderProgramName(), value.getName())) {
						EditorUIUtility.showInfoMessage(GfxMaterialEditor.this, "Shader program in use", "Please detach this shader program from any materials in order to remove it.");
						return false;
					}
				}
				return true;
			}
		});

		materialList.setModel(materialListModel);
		materialList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				MaterialEditHandle handle = materialList.getSelectedValue();
				if (handle != null) {
					shaderProgramSelect.setModel(handle.programSelect);
					uniformListModel.setList(handle.material.shaderParams);
					textureMapperBox.setModel(handle.texMapperListModel);
					addRemoveTextureMapper.bind(textureMapperBox, handle.texMapperListModel, new AddRemoveItemButtons.Handler<UTextureMapper>() {
						@Override
						public UTextureMapper add() {
							String name = EditorUIUtility.callNameInput(GfxMaterialEditor.this, "New texture mapper", "Enter the name for the sampler uniform");
							if (name != null) {
								UTextureMapper mapper = new UTextureMapper();
								mapper.setShaderVariableName(name);
								return mapper;
							}
							return null;
						}

						@Override
						public boolean remove(UTextureMapper value) {
							return true;
						}
					});
				} else {
					shaderProgramSelect.setModel(new DefaultComboBoxModel<>());
					uniformListModel.setList(new ArrayList<>());
					textureMapperBox.setModel(new DefaultComboBoxModel<>());
					addRemoveTextureMapper.bind((JComboBox)null, null, null);
				}
				textureMapperBox.setSelectedIndex(Math.min(0, textureMapperBox.getItemCount() - 1));
			}
		}));

		uniformList.setModel(uniformListModel);

		addRemoveShaderParam.bind(uniformList, uniformListModel, new AddRemoveItemButtons.Handler<UUniform>() {
			@Override
			public UUniform add() {
				String[] options = new String[]{"Float", "Integer", "Vector2", "Vector3", "Vector4"};

				int uniformType = JOptionPane.showOptionDialog(
					GfxMaterialEditor.this,
					"Select the uniform type",
					"New shader parameter",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					"Float"
				);
				if (uniformType == JOptionPane.CLOSED_OPTION) {
					return null;
				} else {
					String uniformName = EditorUIUtility.callNameInput(GfxMaterialEditor.this, "New shader parameter", "Enter a name for the uniform");

					if (uniformName != null) {
						switch (options[uniformType]) {
							case "Float":
								return new UUniformFloat(uniformName);
							case "Integer":
								return new UUniformInt(uniformName);
							case "Vector2":
								return new UUniformVector2(uniformName);
							case "Vector3":
								return new UUniformVector3(uniformName);
							case "Vector4":
								return new UUniformVector4(uniformName);
						}
					}
				}
				return null;
			}

			@Override
			public boolean remove(UUniform value) {
				return true;
			}
		});

		uniformList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				UniformEditHandle hnd = uniformList.getSelectedValue();
				if (hnd != null) {
					uniformEditorHolder.setViewportView((Component) hnd.getEditor());
				} else {
					uniformEditorHolder.setViewportView(null);
				}
			}
		}));

		textureMapperBox.addActionListener((e) -> {
			TextureMapperEditHandle h = (TextureMapperEditHandle)textureMapperBox.getSelectedItem();
			if (h != null) {
				textureMapperEditorHolder.setViewportView(h.editor);
			} else {
				textureMapperEditorHolder.setViewportView(null);
			}
		});

		textureList.setModel(textureListModel);
		
		textureList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				TextureEditHandle h = textureList.getSelectedValue();
				if (h != null) {
					texPreviewHolder.setIcon(h.icon);
				}
				else {
					texPreviewHolder.setIcon(null);
				}
			}
		}));
		
		addRemoveTexture.bind(textureList, textureListModel, new AddRemoveItemButtons.Handler<UTexture>() {
			@Override
			public UTexture add() {
				File f = EditorUIUtility.callFileSelect(GfxMaterialEditor.this, "Image texture | *.png, *.jpg", ".png", ".jpg");
				if (f != null) {
					return IIOTextureLoader.createIIOTexture(f);
				}
				return null;
			}

			@Override
			public boolean remove(UTexture value) {
				for (MaterialEditHandle mat : materialListModel) {
					for (UTextureMapper mapper : mat.material.getTextureMappers()) {
						if (Objects.equals(mapper.getTextureName(), value.getName())) {
							EditorUIUtility.showInfoMessage(GfxMaterialEditor.this, "Texture in use", "Please detach this texture from any texture mappers in order to remove it.");
							return false;
						}
					}
				}
				return true;
			}
		});
	}

	private File savedata_File;
	private USceneNode savedata_SceneNode;

	private void loadResource(File file) {
		USceneNode node = new USceneNode();
		UGfxResource.loadResourceFile(file, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(node));

		shaderList.clearSelection();
		materialList.clearSelection();
		shaderProgramList.clearSelection();
		uniformList.clearSelection();
		textureMapperBox.setSelectedIndex(-1);
		textureList.clearSelection();

		textureListModel.setList(node.textures); //ORDER IS IMPORTANT
		shaderListModel.setList(node.shaders);
		programListModel.setList(node.programs);
		materialListModel.setList(node.materials);

		savedata_SceneNode = node;
		savedata_File = file;
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        materialList = new javax.swing.JList<>();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        textureMapperBox = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        textureMapperEditorHolder = new javax.swing.JScrollPane();
        addRemoveTextureMapper = new urender.demo.editor.AddRemoveItemButtons();
        jPanel10 = new javax.swing.JPanel();
        shaderProgramSelectLabel = new javax.swing.JLabel();
        shaderProgramSelect = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane6 = new javax.swing.JScrollPane();
        uniformList = new javax.swing.JList<>();
        addRemoveShaderParam = new urender.demo.editor.AddRemoveItemButtons();
        uniformEditorHolder = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        shaderProgramList = new javax.swing.JList<>();
        addRemoveProgram = new urender.demo.editor.AddRemoveItemButtons();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        shaderSelectV = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        shaderSelectF = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        shaderList = new javax.swing.JList<>();
        addRemoveShader = new urender.demo.editor.AddRemoveItemButtons();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        shaderEditorArea = new javax.swing.JTextArea();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        textureList = new javax.swing.JList<>();
        addRemoveTexture = new urender.demo.editor.AddRemoveItemButtons();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        texPreviewHolder = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        btnOpen = new javax.swing.JMenuItem();
        btnSave = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("uGFX MaterialEditor");
        setLocationByPlatform(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Material select"));

        jScrollPane1.setViewportView(materialList);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jLabel3.setText("Texture mapper");

        textureMapperEditorHolder.setBorder(null);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(textureMapperEditorHolder)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textureMapperBox, 0, 211, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addRemoveTextureMapper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addRemoveTextureMapper, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(textureMapperBox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textureMapperEditorHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Textures", jPanel9);

        shaderProgramSelectLabel.setText("Shader program");

        shaderProgramSelect.setMaximumRowCount(20);
        shaderProgramSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shaderProgramSelectActionPerformed(evt);
            }
        });

        jScrollPane6.setViewportView(uniformList);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(shaderProgramSelectLabel)
                        .addGap(30, 30, 30)
                        .addComponent(shaderProgramSelect, 0, 267, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(addRemoveShaderParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uniformEditorHolder)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shaderProgramSelectLabel)
                    .addComponent(shaderProgramSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(uniformEditorHolder)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveShaderParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Shader parameters", jPanel10);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane2)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Material editor", jPanel2);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader program select"));

        jScrollPane3.setViewportView(shaderProgramList);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(addRemoveProgram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveProgram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader bindings"));

        jLabel1.setText("Vertex shader");

        shaderSelectV.setMaximumRowCount(20);

        jLabel2.setText("Fragment shader");

        shaderSelectF.setMaximumRowCount(20);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shaderSelectV, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shaderSelectF, 0, 267, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(shaderSelectV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(shaderSelectF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Shader program editor", jPanel5);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader select"));

        jScrollPane2.setViewportView(shaderList);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(addRemoveShader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveShader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader code"));

        shaderEditorArea.setColumns(20);
        shaderEditorArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        shaderEditorArea.setRows(5);
        jScrollPane4.setViewportView(shaderEditorArea);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Shader editor", jPanel3);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Texture select"));

        jScrollPane5.setViewportView(textureList);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(addRemoveTexture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveTexture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));

        texPreviewHolder.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jScrollPane7.setViewportView(texPreviewHolder);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Texture editor", jPanel11);

        fileMenu.setText("File");

        btnOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnOpen.setText("Open GFX file");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        fileMenu.add(btnOpen);

        btnSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        fileMenu.add(btnSave);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
		File f = EditorUIUtility.callFileSelect(this, "UberRender Graphics Resource | *.gfx", ".gfx");
		if (f != null) {
			loadResource(f);
		}
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
		if (savedata_File != null) {
			IEditHandle.saveAll(shaderListModel);
			IEditHandle.saveAll(programListModel);
			IEditHandle.saveAll(materialListModel);
			IEditHandle.saveAll(uniformListModel);
			IEditHandle.saveAll(textureListModel);
			for (MaterialEditHandle mat : materialListModel) {
				IEditHandle.saveAll(mat.texMapperListModel);
			}
			UGfxResource.writeResourceFile(savedata_File, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(savedata_SceneNode));
		}
    }//GEN-LAST:event_btnSaveActionPerformed

    private void shaderProgramSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shaderProgramSelectActionPerformed
		MaterialEditHandle hnd = materialList.getSelectedValue();
		if (hnd != null) {
			hnd.save();
		}
    }//GEN-LAST:event_shaderProgramSelectActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			Logger.getLogger(GfxMaterialEditor.class.getName()).log(Level.SEVERE, null, ex);
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GfxMaterialEditor().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private urender.demo.editor.AddRemoveItemButtons addRemoveProgram;
    private urender.demo.editor.AddRemoveItemButtons addRemoveShader;
    private urender.demo.editor.AddRemoveItemButtons addRemoveShaderParam;
    private urender.demo.editor.AddRemoveItemButtons addRemoveTexture;
    private urender.demo.editor.AddRemoveItemButtons addRemoveTextureMapper;
    private javax.swing.JMenuItem btnOpen;
    private javax.swing.JMenuItem btnSave;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JList<MaterialEditHandle> materialList;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextArea shaderEditorArea;
    private javax.swing.JList<ShaderEditHandle> shaderList;
    private javax.swing.JList<ShaderProgramEditHandle> shaderProgramList;
    private javax.swing.JComboBox<ShaderProgramEditHandle> shaderProgramSelect;
    private javax.swing.JLabel shaderProgramSelectLabel;
    private javax.swing.JComboBox<ShaderEditHandle> shaderSelectF;
    private javax.swing.JComboBox<ShaderEditHandle> shaderSelectV;
    private javax.swing.JLabel texPreviewHolder;
    private javax.swing.JList<TextureEditHandle> textureList;
    private javax.swing.JComboBox<TextureMapperEditHandle> textureMapperBox;
    private javax.swing.JScrollPane textureMapperEditorHolder;
    private javax.swing.JScrollPane uniformEditorHolder;
    private javax.swing.JList<UniformEditHandle> uniformList;
    // End of variables declaration//GEN-END:variables
}
