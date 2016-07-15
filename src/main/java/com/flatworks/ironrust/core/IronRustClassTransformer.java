package com.flatworks.ironrust.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * @author sjx233
 */
public class IronRustClassTransformer implements IClassTransformer, org.objectweb.asm.Opcodes {
    private static final List<String> ajt =
            Collections.unmodifiableList(Arrays.asList("ajt", "net.minecraft.block.Block"));
    private static final List<String> ajt$x =
            Collections.unmodifiableList(Arrays.asList("x", "func_149671_p", "registerBlocks"));
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (checkClass(name, ajt)) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);
            for (MethodNode methodNode : classNode.methods) {
                if (checkMethod(methodNode, ajt$x, "()V")) {
                    InsnList insns = methodNode.instructions;
                    for (AbstractInsnNode insnNode : insns.toArray()) {
                        if (checkLdcString(insnNode, "iron_block")) {
                            AbstractInsnNode n = insnNode.getNext();
                            AbstractInsnNode is = n.getNext().getNext().getNext().getNext();
                            insns.set(n, new TypeInsnNode(NEW,
                                    "com/flatworks/ironrust/block/BlockIronBlock"));
                            insns.set(is,
                                    new MethodInsnNode(INVOKESPECIAL,
                                            "com/flatworks/ironrust/block/BlockIronBlock", "<init>",
                                            "(Lnet/minecraft/block/material/Material;Lnet/minecraft/block/material/MapColor;)V",
                                            false));
                        }
                    }
                }
            }
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return basicClass;
    }
    
    private static boolean checkClass(String className, List<String> names) {
        return names.contains(className);
    }
    
    private static boolean checkMethod(MethodNode node, List<String> names, String desc) {
        return names.contains(node.name) && node.desc.equals(desc);
    }
    
    private static boolean checkLdcString(AbstractInsnNode node, String constant) {
        return node.getOpcode() == LDC && constant.equals(((LdcInsnNode) node).cst);
    }
}
