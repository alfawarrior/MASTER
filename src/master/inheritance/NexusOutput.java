/*
 * Copyright (C) 2012 Tim Vaughan <tgvaughan@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package master.inheritance;

import java.io.PrintStream;

/**
 * Class containing static methods useful for exporting tree-like graphs
 * using the NEXUS format.  An advantage of this format over the vanilla
 * Newick format is that it allows annotations describing the population
 * type and location of each node.
 * 
 * <p>Note that extended newick strings will be used if the graph is not
 * tree-like in the direction it's traversed.</p>
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class NexusOutput extends NewickOutput {
    
    /**
     * Constructor.
     * 
     * @param graph Graph to represent.
     * @param reverseTime True causes the graph to be read in the direction
     * from the latest nodes to the earliest.  Useful for coalescent trees.
     * @param collapseSingleChildNodes 
     */
    public NexusOutput(InheritanceTrajectory graph, boolean reverseTime,
            boolean collapseSingleChildNodes, PrintStream pstream) {
        super(graph, reverseTime, collapseSingleChildNodes, pstream);
    }
    
    @Override
    protected void addLabel(Node node, double branchLength) {
        
        if (leafLabels.containsKey(node))
            ps.append(leafLabels.get(node));
        
        if (hybridIDs.containsKey(node))
            ps.append("#").append(String.valueOf(hybridIDs.get(node)));
        // note that we've omitted the optional "type" specifier
        
        // Annotations traditionally refer to the branch _above_ the node
        // on the tree.  The following correction ensures this tradition is
        // followed when a tree is generated by reading a graph in reverse.
        Node branchNode;
        if (reverseTime) {
            branchNode = node.getChildren().get(0);
        } else
            branchNode = node;
        
        ps.append("[&");
        ps.append("type=").append(branchNode.population.getType().getName());
        if (!branchNode.population.isScalar()) {
            ps.append(",location=");

            int[] loc = branchNode.population.getLocation();
            for (int i=0; i<loc.length; i++) {
                if (i>0)
                    ps.append("_");
                ps.append(String.valueOf(loc[i]));
            }
        }
        if (node.reactionGroup != null && node.reactionGroup.getName() != null) {
            ps.append(",reaction=").append(node.reactionGroup.getName());
        }
        
        // Add general annotations:
        if (node.getAttributeNames() != null) {
            for (String name : node.getAttributeNames()) {
                Object value = node.getAttribute(name);
                
                if (value instanceof Integer) {
                    ps.append("," + name + "=" + String.valueOf((Integer)value));
                    continue;
                }
                
                if (value instanceof Double) {
                    ps.append("," + name + "=" + String.valueOf((Double)value));
                    continue;
                }
                                
                if (value instanceof Boolean) {
                    ps.append("," + name + "=" + String.valueOf((Boolean)value));
                    continue;
                }
                                
                if (value instanceof String) {
                    ps.append("," + name + "=" + (String)value);
                    continue;
                }
            }
        }
        
        ps.append("]");
        
        ps.append(":").append(String.valueOf(branchLength));
    }
    
    /**
     * Write extended Newick representation of graph to PrintStream pstream
     * with a NEXUS wrapper.  Note that in this representation nodes are
     * annotated with details of the population they belong to.
     * 
     * @param itraj Inheritance trajectory to represent.
     * @param reverseTime Whether to traverse tree in backward time.
     * @param collapseSingleChildNodes 
     * @param pstream PrintStream object to which result is sent.
     */
    public static void write(InheritanceTrajectory itraj,
            boolean reverseTime, boolean collapseSingleChildNodes, PrintStream pstream) {
        
        if (itraj.getSpec().getVerbosity()>0)
            System.out.println("Writing NEXUS file...");
        
        pstream.println("#nexus\n\nBegin trees;");
        
        // Skip empty inheritance graphs:
        if (!itraj.getStartNodes().isEmpty()) {
            pstream.print("tree TREE = ");
            new NexusOutput(itraj, reverseTime, collapseSingleChildNodes, pstream);
            pstream.println();
        }
        
        pstream.println("End;");
    }
    
    /**
     * Write extended Newick representation of an ensemble of graphs to
     * PrintStream pstream with a NEXUS wrapper.  Note that in this
     * representation nodes are annotated with details of the population
     * they belong to, and the reaction that generated them.
     * 
     * @param iensemble Ensemble of inheritance graphs to represent.
     * @param reverseTime Whether to traverse tree in backward time.
     * @param collapseSingleChildNodes 
     * @param pstream PrintStream object to which result is sent.
     */
    public static void write(InheritanceEnsemble iensemble,
            boolean reverseTime, boolean collapseSingleChildNodes, PrintStream pstream) {
        
        if (iensemble.getSpec().getVerbosity()>0)
            System.out.println("Writing NEXUS file...");
                
        pstream.println("#nexus\n\nBegin trees;");
        
        for (int i=0; i<iensemble.itrajectories.size(); i++) {
            InheritanceTrajectory thisTraj = iensemble.itrajectories.get(i);
            
            // Skip empty inheritance graphs:
            if (thisTraj.getStartNodes().isEmpty())
                continue;
            
            pstream.print("tree TREE_" + i + " = ");
            new NexusOutput(thisTraj, reverseTime, collapseSingleChildNodes, pstream);
            pstream.println();
        }
        pstream.println("End;");
    }
}
