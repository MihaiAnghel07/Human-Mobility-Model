import matplotlib.pyplot as plt
import matplotlib.cm as cm
import numpy as np
import re

def parse_file(file_path):
    node_positions = {}  # {node_id: [list of positions]}
    current_positions = {}
    
    with open(file_path, 'r') as f:
        lines = f.readlines()
    
    move_pattern = re.compile(r'Node\[(\d+)\] moved from \[(\d+), (\d+)\] to \[(\d+), (\d+)\]')
    stay_pattern = re.compile(r'Node\[(\d+)\] stays')
    
    for line in lines:
        line = line.strip()
        move_match = move_pattern.search(line)
        stay_match = stay_pattern.search(line)
        
        if move_match:
            node_id = int(move_match.group(1))
            new_x = int(move_match.group(4))
            new_y = int(move_match.group(5))
            current_positions[node_id] = (new_x, new_y)
            if node_id not in node_positions:
                node_positions[node_id] = []
            node_positions[node_id].append((new_x, new_y))
            
        elif stay_match:
            node_id = int(stay_match.group(1))
            if node_id in current_positions:
                node_positions[node_id].append(current_positions[node_id])
    
    return node_positions

def plot_movements(node_positions, context_data=None):
    size = (50, 50)
    if context_data:
        size, homes, pubs, others, obstacles = context_data
    else:
        homes = pubs = others = obstacles = []

    colors = cm.get_cmap('tab10', len(node_positions))
    plt.figure(figsize=(10, 10))

    # Plot node movements
    for idx, (node_id, positions) in enumerate(node_positions.items()):
        if not positions:
            continue
        xs, ys = zip(*positions)
        plt.plot(xs, ys, marker='o', label=f'Node {node_id}', color=colors(idx))

    # Draw homes
    for node_id, (hx, hy) in homes.items():
        plt.scatter(hx, hy, color='blue', marker='H', s=130, label='Home' if node_id == 1 else "")

    # Draw pubs
    for (px, py) in pubs:
        plt.scatter(px, py, color='green', marker='P', s=130, label='Pub' if pubs.index((px, py)) == 0 else "")

    # Draw others
    for (ox, oy) in others:
        plt.scatter(ox, oy, color='purple', marker='^', s=130, label='Other' if others.index((ox, oy)) == 0 else "")

    # Draw obstacles
    for (ox, oy) in obstacles:
        plt.scatter(ox, oy, color='black', marker='s', s=20, label='Obstacle' if obstacles.index((ox, oy)) == 0 else "")

    plt.xlim(0, size[0])
    plt.ylim(0, size[1])
    plt.grid(True)
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.title('Node Movements & Context')
    # Unic label pentru fiecare tip
    handles, labels = plt.gca().get_legend_handles_labels()
    by_label = dict(zip(labels, handles))
    plt.legend(by_label.values(), by_label.keys())
    plt.show()
    colors = cm.get_cmap('tab10', len(node_positions))  # o culoare diferită pentru fiecare nod
    
    plt.figure(figsize=(10, 10))
    
    for idx, (node_id, positions) in enumerate(node_positions.items()):
        if not positions:
            continue
        xs, ys = zip(*positions)
        plt.plot(xs, ys, marker='o', label=f'Node {node_id}', color=colors(idx))
    
    plt.xlim(0, 50)  # setează axa X între 0 și 50
    plt.ylim(0, 50)  # setează axa Y între 0 și 50
    plt.grid(True)
    plt.legend()
    plt.title('Node Movements Over Time')
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.show()

def read_context_file(context_path):
    nodes = {}
    pubs = []
    others = []
    obstacles = []
    size = (50, 50)  # default fallback

    with open(context_path, 'r') as f:
        lines = f.readlines()

    i = 0
    while i < len(lines):
        line = lines[i].strip()

        if line.startswith('#dimensiunea'):
            size = tuple(map(int, lines[i+1].strip().split()))
            i += 2
        elif line.startswith('#nodurile'):
            i += 1
            while i < len(lines) and not lines[i].startswith('#'):
                node_id, home_x, home_y, *_ = map(int, lines[i].split())
                nodes[node_id] = (home_x, home_y)
                i += 1
                print(i)
        elif line.startswith('#pub-urile'):
            i += 1
            while i < len(lines) and not lines[i].startswith('#'):
                _, pub_x, pub_y = map(int, lines[i].split())
                pubs.append((pub_x, pub_y))
                i += 1
        elif line.startswith('#others'):
            i += 1
            while i < len(lines) and not lines[i].startswith('#'):
                others.append(tuple(map(int, lines[i].split())))
                i += 1
        elif line.startswith('#obstacole'):
            i += 1
            while i < len(lines):
                obstacles.append(tuple(map(int, lines[i].split())))
                i += 1
        else:
            i += 1

    return size, nodes, pubs, others, obstacles

if __name__ == "__main__":
    output_path = 'output/output.txt'
    context_path = 'input/input.txt'
    
    node_positions = parse_file(output_path)
    context_data = read_context_file(context_path)
    
    plot_movements(node_positions, context_data)
