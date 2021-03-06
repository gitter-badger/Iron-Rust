package com.flatworks.ironrust.core;

import java.util.Arrays;
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

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * @author sjx233
 */
public class IronRustClassTransformer implements IClassTransformer, org.objectweb.asm.Opcodes {
    private static final List<String> Block = Arrays.asList("akf", "net.minecraft.block.Block");
    private static final List<String> func_149671_p =
            Arrays.asList("x", "func_149671_p", "registerBlocks");
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (checkClass(name, Block)) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);
            boolean found = false;
            for (MethodNode methodNode : classNode.methods) {
                if (checkMethod(methodNode, func_149671_p, "()V")) {
                    InsnList insns = methodNode.instructions;
                    for (AbstractInsnNode insnNode : insns.toArray()) {
                        if (checkLdcString(insnNode, "iron_block")) {
                            found = true;
                            AbstractInsnNode n = insnNode.getNext();
                            AbstractInsnNode is = n.getNext().getNext().getNext().getNext();
                            insns.set(n, new TypeInsnNode(NEW,
                                    "com/flatworks/ironrust/block/BlockIron"));
                            insns.set(is,
                                    new MethodInsnNode(INVOKESPECIAL,
                                            "com/flatworks/ironrust/block/BlockIron", "<init>",
                                            "(Lnet/minecraft/block/material/Material;Lnet/minecraft/block/material/MapColor;)V",
                                            false));
                        }
                    }
                }
            }
            if (!found) {
                IronRustMod.logger.error("There was errors running the class transformer.");
            }
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return basicClass;
    }
    
    private static boolean checkClass(String name, List<String> names) {
        return names.contains(name);
    }
    
    private static boolean checkMethod(MethodNode node, List<String> names, String desc) {
        return names.contains(node.name) && desc.equals(node.desc);
    }
    
    private static boolean checkLdcString(AbstractInsnNode node, String constant) {
        return node.getOpcode() == LDC && constant.equals(((LdcInsnNode) node).cst);
    }
}
