from collections import deque

def parse_input(file_path):
    """Parses the input file to get the towel patterns and desired designs."""
    with open(file_path, 'r') as f:
        lines = f.read().splitlines()

    # Split patterns and designs
    blank_line_index = lines.index('')
    patterns = lines[0].split(', ')  # Split the first line by ', '
    designs = lines[blank_line_index + 1:]

    return patterns, designs

def can_form_design(patterns, design):
    """Checks if a design can be formed using the available patterns."""
    # Convert patterns into a set for quick lookup
    pattern_set = set(patterns)

    # BFS to check if we can form the design
    queue = deque([design])
    visited = set()  # To avoid re-checking the same substring

    while queue:
        current = queue.popleft()

        if not current:  # If empty, we successfully formed the design
            return True

        if current in visited:  # Skip already visited states
            continue
        visited.add(current)

        for pattern in pattern_set:
            if current.startswith(pattern):
                queue.append(current[len(pattern):])

    return False

def count_possible_designs(patterns, designs):
    """Counts how many designs can be formed with the available patterns."""
    count = 0
    for design in designs:
        if can_form_design(patterns, design):
            count += 1
    return count

def count_all_arrangements(patterns, design):
    """Counts all possible arrangements to form a design using the available patterns."""
    # Convert patterns into a set for quick lookup
    pattern_set = set(patterns)

    # Dynamic programming approach
    dp = [0] * (len(design) + 1)
    dp[0] = 1  # Base case: one way to form an empty string

    for i in range(1, len(design) + 1):
        for pattern in pattern_set:
            if design[:i].endswith(pattern):
                dp[i] += dp[i - len(pattern)]

    return dp[len(design)]

def sum_all_arrangements(patterns, designs):
    """Sums up all possible arrangements for all designs."""
    total_arrangements = 0
    for design in designs:
        total_arrangements += count_all_arrangements(patterns, design)
    return total_arrangements

if __name__ == "__main__":
    file_path = "2024_day19_input.txt"

    patterns, designs = parse_input(file_path)

    print(f"Patterns: {patterns}")
    print(f"Designs: {designs}")

    possible_count = count_possible_designs(patterns, designs)
    print(f"Number of possible designs: {possible_count}")

    total_arrangements = sum_all_arrangements(patterns, designs)
    print(f"Total number of arrangements: {total_arrangements}")
