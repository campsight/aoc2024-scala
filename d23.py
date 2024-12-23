#!/usr/bin/env python3

import networkx as nx


def main():
    input_file = "2024_day23_input.txt"

    G = nx.Graph()

    # Read edges from file (each line is "X-Y" for an undirected edge)
    with open(input_file, "r") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            u, v = line.split('-')
            G.add_edge(u, v)

    # --- Part 1 ---
    all_cliques = nx.enumerate_all_cliques(G)
    triangles = [c for c in all_cliques if len(c) == 3]
    triangles_with_t = [tri for tri in triangles if any(node.startswith('t') for node in tri)]
    print("Part 1 result (number of 3-cliques containing a 't'):", len(triangles_with_t))

    # --- Part 2 ---
    # Find the largest set of mutually connected computers (the maximum clique).
    # nx.find_cliques(G) yields all maximal cliques; we choose the one with the greatest length.
    max_clique = max(nx.find_cliques(G), key=len)  # largest clique by size

    # Sort the computer names alphabetically and join them with commas
    password = ",".join(sorted(max_clique))

    print("Part 2 result (LAN party password):", password)


if __name__ == "__main__":
    main()
