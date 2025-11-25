import pandas as pd
import matplotlib.pyplot as plt

def load_and_inspect_data(filepath):
    """
    Loads the dataset and prints basic information.
    """
    print(f"--- Loading data from {filepath} ---")
    try:
        df = pd.read_csv(filepath)
        print("Data loaded successfully!")
        
        print("\n--- First 5 rows ---")
        print(df.head())
        
        print("\n--- Data Info ---")
        print(df.info())
        
        print("\n--- Descriptive Statistics ---")
        print(df.describe())
        
        return df
    except FileNotFoundError:
        print(f"Error: File not found at {filepath}")
        return None

def analyze_and_visualize(df):
    """
    Performs filtering, sorting, and visualization.
    """
    if df is None:
        return

    # 1. Filtering: Successful projects
    print("\n--- Filtering: Successful Projects ---")
    successful_projects = df[df['state'] == 'successful']
    print(f"Number of successful projects: {len(successful_projects)}")

    # 2. Sorting: Top 5 most funded projects
    print("\n--- Sorting: Top 5 Most Funded Projects ---")
    top_funded = successful_projects.sort_values(by='usd_pledged_real', ascending=False)
    print(top_funded[['name', 'main_category', 'usd_pledged_real']].head(5))

    # 3. Visualization: Top 10 Categories
    print("\n--- Generating Visualizations ---")
    
    # Bar Chart: Top 10 Categories by Count
    plt.figure(figsize=(12, 6))
    category_counts = df['main_category'].value_counts().head(10)
    category_counts.plot(kind='bar', color='skyblue')
    plt.title('Top 10 Kickstarter Categories')
    plt.xlabel('Category')
    plt.ylabel('Number of Projects')
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.savefig('python_learning/top_categories.png')
    print("Saved top_categories.png")

    # Pie Chart: Project States
    plt.figure(figsize=(8, 8))
    state_counts = df['state'].value_counts()
    state_counts.plot(kind='pie', autopct='%1.1f%%', startangle=90)
    plt.title('Distribution of Project States')
    plt.ylabel('')
    plt.savefig('python_learning/project_states.png')
    print("Saved project_states.png")

if __name__ == "__main__":
    file_path = "python_learning/kickstarter_2018.csv"
    df = load_and_inspect_data(file_path)
    analyze_and_visualize(df)
