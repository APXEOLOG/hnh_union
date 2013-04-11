package union;

import java.util.*;
import java.util.Vector;

public class AStar {

	public class Agent {
	}

	public static class Location {

		public int x, y; // max values 999 and 999

		public Location(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public boolean equals(Object loc) {
			return ((Location) loc).x == x && ((Location) loc).y == y;
		}

		public int hashCode() {
			return 1000 * x + y;
		}
	}

	public class Node {

		Location location;
		double costFromStart;
		double costToGoal;
		double totalCost;
		Node parent;

		public boolean equals(Object o) {
			if (o instanceof Node) {
				return location.equals(((Node) o).location);
			} else {
				return false;
			}
		}
	}

	public class PQ {

		Vector q = new Vector();

		public Node pop() {
			Node node = (Node) q.elementAt(0);
			q.removeElement(node);
			return node;
		}

		public void add(Node node) {
			if (q.size() == 0) {
				q.addElement(node);
			} else {
				int i;
				for (i = 0; i < q.size(); i++) {
					Node holder = (Node) q.elementAt(i);
					if (holder.totalCost >= node.totalCost) {
						q.insertElementAt(node, i);
						break;
					}
				}
				if (i == q.size()) {
					q.addElement(node);
				}
			}
		}

		public boolean contains(Node node) {
			return q.contains(node);
		}

		public void remove(Node node) {
			q.removeElement(node);
		}

		public int size() {
			return q.size();
		}
	}

	public class Constants {
		public static final int NOTHING = -1, EMPTY = 0, TERRAIN = 1, FULL_NON_PASSABLE = 2, DIAGONAL_NON_PASSABLE = 3, START = 4, FINISH = 5,
				NUMCOLORS = FINISH + 1;
	}

	double typicalCost; // default one
	Hashtable open;
	Hashtable closed;

	int[][] grid;
	int[] costs;
	Location startLoc, goalLoc;

	public AStar(int[][] grid, Location startLoc, Location goalLoc) {
		this.grid = grid;
		this.costs = new int[6];
		costs[Constants.EMPTY] = 1;
		costs[Constants.TERRAIN] = 5;
		costs[Constants.DIAGONAL_NON_PASSABLE] = Constants.NOTHING;
		costs[Constants.FULL_NON_PASSABLE] = Constants.NOTHING;
		costs[Constants.START] = 1;
		costs[Constants.FINISH] = 1;
		this.startLoc = startLoc;
		this.goalLoc = goalLoc;
		this.typicalCost = getTypicalCost(new Location(0, 0), new Location(
				grid.length - 1, grid[0].length - 1));
		this.open = new Hashtable(grid.length * grid[0].length);
		this.closed = new Hashtable(grid.length * grid[0].length);
	}

	private double getTypicalCost(Location startLoc, Location goalLoc) {
		int left = Math.min(startLoc.x, goalLoc.x);
		int top = Math.min(startLoc.y, goalLoc.y);
		int right = Math.max(startLoc.x, goalLoc.x);
		int bottom = Math.max(startLoc.y, goalLoc.y);

		int count = 0;
		int sum = 0;
		for (int i = left; i <= right; i++) {
			for (int j = top; j <= bottom; j++) {
				int value = grid[i][j];
				if (value != Constants.NOTHING) {
					sum += costs[value];
					count++;
				}
			}
		}

		return (double) sum / (double) count / 1.1;
	}

	public Vector AStarSearch(Agent agent) {

		PQ openQ = new PQ();

		// initialize a start node
		Node startNode = new Node();
		startNode.location = startLoc;
		startNode.costFromStart = 0;
		startNode.costToGoal = pathCostEstimate(startLoc, goalLoc, agent);
		startNode.totalCost = startNode.costFromStart + startNode.costToGoal;
		startNode.parent = null;

		openQ.add(startNode);
		open.put(startNode.location, startNode);

		// process the list until success or failure
		while (openQ.size() > 0) {

			Node node = openQ.pop();
			open.remove(node.location);

			// if at a goal, we're done
			if (node.location.equals(goalLoc)) {
				return solve(node);
			} else {
				Vector neighbors = getNeighbors(node);
				for (int i = 0; i < neighbors.size(); i++) {
					Node newNode = (Node) neighbors.elementAt(i);
					double newCostEstimate = pathCostEstimate(newNode.location,
							goalLoc, agent);
					double newCost = node.costFromStart
							+ traverseCost(node, newNode, agent);
					double newTotal = newCost + newCostEstimate;

					Location nnLoc = newNode.location;
					Node holderO, holderC;
					holderO = (Node) open.get(nnLoc);
					holderC = (Node) closed.get(nnLoc);
					if (holderO != null && holderO.totalCost <= newTotal) {
						continue;
					} else if (holderC != null && holderC.totalCost <= newTotal) {
						continue;
					} else {
						// store the new or improved info
						newNode.parent = node;
						newNode.costFromStart = newCost;
						newNode.costToGoal = newCostEstimate;
						newNode.totalCost = newNode.costFromStart
								+ newNode.costToGoal;
						if (closed.get(nnLoc) != null) {
							closed.remove(nnLoc);
						}
						Node check = (Node) open.get(nnLoc);
						if (check != null) {
							openQ.remove(check);
							open.remove(nnLoc);
						}
						openQ.add(newNode);
						open.put(nnLoc, newNode);

					} // now done with node
				}
				closed.put(node.location, node);
			}
		}
		return null; // failure
	}

	private Vector getNeighbors(Node node) {
		Location nodeLoc = node.location;
		Vector neighbors = new Vector();
		addConditional(neighbors, nodeLoc, -1, 0);
		addConditional(neighbors, nodeLoc, 0, -1);
		addConditional(neighbors, nodeLoc, 0, 1);
		addConditional(neighbors, nodeLoc, 1, 0);
		// Diagonal moving
//		addConditional(neighbors, nodeLoc, 1, 1);
//		addConditional(neighbors, nodeLoc, -1, -1);
//		addConditional(neighbors, nodeLoc, -1, 1);
//		addConditional(neighbors, nodeLoc, 1, -1);

		return neighbors;
	}
	
	private boolean isDiagonal(int x, int y) {
		return x != 0 && y != 0;
	}
	
	private void addConditional(Vector addTo, Location loc, int x, int y) {
		int newX = loc.x + x, newY = loc.y + y;
		if (newX < 0 || newX >= grid.length) {
			return;
		}
		if (newY < 0 || newY >= grid[0].length) {
			return;
		}
		if (grid[newX][newY] == Constants.FULL_NON_PASSABLE) {
			return;
		}
		if (isDiagonal(newX, newY)) {
			if (grid[newX][newY] == Constants.DIAGONAL_NON_PASSABLE) {
				return;
			}
		}
		
		Node newNode = new Node();
		newNode.location = new Location(newX, newY);
		addTo.addElement(newNode);
	}

	private double pathCostEstimate(Location start, Location goal, Agent agent) {
		if (agent == null) { // default agent
			int dx = Math.abs(goal.x - start.x);
			int dy = Math.abs(goal.y - start.y);
			double diff = (double) Math.abs(dx - dy);

			return typicalCost * (dx + dy);
		} else {
			return 1;
		}
	}

	private double traverseCost(Node node, Node newNode, Agent agent) {
		if (agent == null) { // default agent
			Location loc1 = node.location, loc2 = newNode.location;
			int dx = Math.abs(loc1.x - loc2.x), dy = Math.abs(loc1.y - loc2.y);
			return costs[grid[newNode.location.x][newNode.location.y]] + 0.1
					* (dx + dy - 1);
		} else {
			return 1;
		}
	}

	private Vector solve(Node node) {
		Vector solution = new Vector();

		solution.addElement(node);
		while (node.parent != null) {
			solution.insertElementAt(node.parent, 0);
			node = node.parent;
		}

		return solution;
	}
}
