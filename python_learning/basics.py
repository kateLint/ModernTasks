def analyze_project_list(projects):
    """
    Demonstrates lists, loops, and basic string manipulation.
    """
    print(f"--- Analyzing {len(projects)} Projects ---")
    for project in projects:
        # String manipulation
        clean_name = project.strip().title()
        print(f"Project: {clean_name}")

def analyze_project_stats(stats):
    """
    Demonstrates dictionaries and conditional logic.
    """
    print("\n--- Project Statistics ---")
    total_pledged = 0
    successful_projects = 0

    for name, pledged in stats.items():
        print(f"{name}: ${pledged}")
        total_pledged += pledged
        
        if pledged > 1000:
            successful_projects += 1

    print(f"\nTotal Pledged: ${total_pledged}")
    print(f"Successful Projects (> $1000): {successful_projects}")

if __name__ == "__main__":
    # Lists
    my_projects = ["  super cool gadget  ", "new board game", "indie movie"]
    
    # Dictionaries
    project_pledges = {
        "Super Cool Gadget": 15000,
        "New Board Game": 500,
        "Indie Movie": 2500
    }

    # Function calls
    analyze_project_list(my_projects)
    analyze_project_stats(project_pledges)
