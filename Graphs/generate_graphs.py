import os
import tempfile
import csv

# Set writable cache directory for Matplotlib
os.environ['MPLCONFIGDIR'] = tempfile.mkdtemp()
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

os.makedirs('Graphs', exist_ok=True)

# -------------------------------------------------------------
# GRAPH 1: Q1 String Performance (Execution Time & Scaling)
# -------------------------------------------------------------
print("Generating Q1 Graphs...")
q1_csv = 'Experimental_Data/Q1_string_performance.csv'
q1_data = []
with open(q1_csv, 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        q1_data.append(row)

# Filter Workload 2
w2_data = [r for r in q1_data if 'Workload 2' in r['Workload']]
if w2_data:
    approaches = [r['Approach'] for r in w2_data]
    times = [float(r['AvgTimeMs']) for r in w2_data]
    colors = ['#e74c3c', '#2ecc71', '#3498db', '#9b59b6']

    fig, ax = plt.subplots(figsize=(8, 5))
    bars = ax.bar(approaches, times, color=colors[:len(approaches)], edgecolor='black', alpha=0.85)
    ax.set_ylabel('Average Execution Time (ms)', fontsize=12, fontweight='bold')
    ax.set_title('Q1: 10,000 Tickets (50 Fields Each) - Execution Time Comparison', fontsize=13, fontweight='bold')
    ax.grid(axis='y', linestyle='--', alpha=0.7)
    for bar in bars:
        yval = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2.0, yval + 1, f'{yval:.2f} ms', ha='center', va='bottom', fontweight='bold')
    plt.tight_layout()
    plt.savefig('Graphs/Q1_execution_time_comparison.png', dpi=300)
    plt.close()

# Q1 Memory and Object Churn
if w2_data:
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 5))
    objs = [int(r['EstIntermediateObjects']) for r in w2_data]
    mems = [float(r['MemoryUsedMB']) for r in w2_data]

    ax1.bar(approaches, objs, color=['#e74c3c', '#2ecc71', '#3498db', '#9b59b6'], edgecolor='black', alpha=0.85)
    ax1.set_ylabel('Intermediate Objects Created', fontsize=11, fontweight='bold')
    ax1.set_title('Intermediate String Objects (Workload 2)', fontsize=12, fontweight='bold')
    ax1.set_yscale('log')
    ax1.grid(axis='y', linestyle='--', alpha=0.7)

    ax2.bar(approaches, mems, color=['#e74c3c', '#2ecc71', '#3498db', '#9b59b6'], edgecolor='black', alpha=0.85)
    ax2.set_ylabel('Heap Memory Consumed (MB)', fontsize=11, fontweight='bold')
    ax2.set_title('Heap Memory Usage (Workload 2)', fontsize=12, fontweight='bold')
    ax2.grid(axis='y', linestyle='--', alpha=0.7)

    plt.tight_layout()
    plt.savefig('Graphs/Q1_memory_and_objects.png', dpi=300)
    plt.close()

# -------------------------------------------------------------
# GRAPH 2: Q2 Playlist Operations (ArrayList vs LinkedList)
# -------------------------------------------------------------
print("Generating Q2 Graphs...")
q2_csv = 'Experimental_Data/Q2_playlist_operations.csv'
q2_data = []
with open(q2_csv, 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        q2_data.append(row)

# Get-kth element scaling
sizes = [1000, 10000, 100000]
al_get = [float(r['AvgTimePerOpNs']) for r in q2_data if r['Collection'] == 'ArrayList' and r['Operation'] == 'Access k-th Element']
ll_get = [float(r['AvgTimePerOpNs']) for r in q2_data if r['Collection'] == 'LinkedList' and r['Operation'] == 'Access k-th Element']

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13, 5))

ax1.plot(sizes, al_get, marker='o', linewidth=2.5, color='#2980b9', label='ArrayList (O(1))')
ax1.plot(sizes, ll_get, marker='s', linewidth=2.5, color='#e67e22', label='LinkedList (O(N))')
ax1.set_xscale('log')
ax1.set_yscale('log')
ax1.set_xlabel('Playlist Size (N)', fontsize=11, fontweight='bold')
ax1.set_ylabel('Latency per get(k) (ns - log scale)', fontsize=11, fontweight='bold')
ax1.set_title('Random Access Latency: get(k)', fontsize=12, fontweight='bold')
ax1.grid(True, linestyle='--', alpha=0.6)
ax1.legend(fontsize=10)

# Insert at Front
al_front = [float(r['AvgTimePerOpNs']) for r in q2_data if r['Collection'] == 'ArrayList' and r['Operation'] == 'Insert at Front']
ll_front = [float(r['AvgTimePerOpNs']) for r in q2_data if r['Collection'] == 'LinkedList' and r['Operation'] == 'Insert at Front']

ax2.plot(sizes, al_front, marker='o', linewidth=2.5, color='#2980b9', label='ArrayList (O(N) Shift)')
ax2.plot(sizes, ll_front, marker='s', linewidth=2.5, color='#27ae60', label='LinkedList (O(1) Pointer)')
ax2.set_xscale('log')
ax2.set_xlabel('Playlist Size (N)', fontsize=11, fontweight='bold')
ax2.set_ylabel('Latency per Insert at Front (ns)', fontsize=11, fontweight='bold')
ax2.set_title('Insert at Front Latency', fontsize=12, fontweight='bold')
ax2.grid(True, linestyle='--', alpha=0.6)
ax2.legend(fontsize=10)

plt.tight_layout()
plt.savefig('Graphs/Q2_operation_latency_vs_size.png', dpi=300)
plt.close()

# -------------------------------------------------------------
# GRAPH 3: Q4 Pharmacy Inventory Maps
# -------------------------------------------------------------
print("Generating Q4 Graphs...")
q4_csv = 'Experimental_Data/Q4_map_benchmarks.csv'
q4_data = []
with open(q4_csv, 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        q4_data.append(row)

# Filter for size 100,000
q4_100k = [r for r in q4_data if r['InventorySize'] == '100000']
if q4_100k:
    maps = [r['MapType'] for r in q4_100k]
    lookup_ns = [float(r['AvgLookupNs']) for r in q4_100k]
    code_rep = [float(r['CodeReportTimeMs']) for r in q4_100k]
    ins_rep = [float(r['InsertionReportTimeMs']) for r in q4_100k]

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13, 5))

    # Lookup latency
    bars1 = ax1.bar(maps, lookup_ns, color=['#3498db', '#e74c3c', '#2ecc71'], edgecolor='black', alpha=0.85)
    ax1.set_ylabel('Lookup Latency (ns / op)', fontsize=11, fontweight='bold')
    ax1.set_title('100,000 Lookups Latency (N=100,000)', fontsize=12, fontweight='bold')
    ax1.grid(axis='y', linestyle='--', alpha=0.7)
    for bar in bars1:
        yval = bar.get_height()
        ax1.text(bar.get_x() + bar.get_width()/2.0, yval + 10, f'{yval:.1f} ns', ha='center', va='bottom', fontweight='bold')

    # Reporting times
    import numpy as np
    x = np.arange(len(maps))
    width = 0.35
    rects1 = ax2.bar(x - width/2, code_rep, width, label='Code Order Report (ms)', color='#9b59b6', edgecolor='black', alpha=0.85)
    rects2 = ax2.bar(x + width/2, ins_rep, width, label='Insertion Order Report (ms)', color='#f1c40f', edgecolor='black', alpha=0.85)
    ax2.set_xticks(x)
    ax2.set_xticklabels(maps)
    ax2.set_ylabel('Generation Time (ms)', fontsize=11, fontweight='bold')
    ax2.set_title('Evening Report Generation Time (N=100,000)', fontsize=12, fontweight='bold')
    ax2.legend(fontsize=10)
    ax2.grid(axis='y', linestyle='--', alpha=0.7)

    plt.tight_layout()
    plt.savefig('Graphs/Q4_lookup_and_report_latency.png', dpi=300)
    plt.close()

# -------------------------------------------------------------
# GRAPH 4: Q6 Triage Queue Throughput
# -------------------------------------------------------------
print("Generating Q6 Graphs...")
q6_csv = 'Experimental_Data/Q6_triage_scaling.csv'
q6_data = []
with open(q6_csv, 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        q6_data.append(row)

if q6_data:
    q_sizes = [int(r['QueueSize']) for r in q6_data]
    admit_lat = [float(r['AvgAdmissionNs']) for r in q6_data]
    poll_lat = [float(r['AvgDispatchNs']) for r in q6_data]

    fig, ax = plt.subplots(figsize=(8, 5))
    ax.plot(q_sizes, admit_lat, marker='o', linewidth=2.5, color='#2980b9', label='Admission Latency (O(log N))')
    ax.plot(q_sizes, poll_lat, marker='s', linewidth=2.5, color='#c0392b', label='Dispatch / Poll Latency (O(log N))')
    ax.set_xscale('log')
    ax.set_xlabel('Triage Queue Size (Patients)', fontsize=11, fontweight='bold')
    ax.set_ylabel('Latency (ns / op)', fontsize=11, fontweight='bold')
    ax.set_title('Q6: PriorityQueue Scaling Performance (Hospital Triage)', fontsize=13, fontweight='bold')
    ax.grid(True, linestyle='--', alpha=0.6)
    ax.legend(fontsize=10)
    plt.tight_layout()
    plt.savefig('Graphs/Q6_heap_throughput.png', dpi=300)
    plt.close()

# -------------------------------------------------------------
# GRAPH 5: Q7 Original vs Redesigned Triage Architecture
# -------------------------------------------------------------
print("Generating Q7 Graphs...")
q7_csv = 'Experimental_Data/Q7_comparative_validation.csv'
q7_data = []
with open(q7_csv, 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        q7_data.append(row)

if q7_data:
    workload_sizes = [1000, 5000, 10000, 25000]
    orig_times = [float(r['TotalTimeMs']) for r in q7_data if r['Architecture'] == 'Original PriorityQueue']
    red_times = [float(r['TotalTimeMs']) for r in q7_data if r['Architecture'] == 'Redesigned AF-MLBQ']
    orig_aging = [float(r['TotalAgingTimeMs']) for r in q7_data if r['Architecture'] == 'Original PriorityQueue']
    red_aging = [float(r['TotalAgingTimeMs']) for r in q7_data if r['Architecture'] == 'Redesigned AF-MLBQ']

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(13, 5))

    ax1.plot(workload_sizes, orig_times, marker='o', linewidth=2.5, color='#c0392b', label='Original PriorityQueue')
    ax1.plot(workload_sizes, red_times, marker='s', linewidth=2.5, color='#27ae60', label='Redesigned AF-MLBQ')
    ax1.set_xlabel('Workload Operations', fontsize=11, fontweight='bold')
    ax1.set_ylabel('Total Execution Time (ms)', fontsize=11, fontweight='bold')
    ax1.set_title('Overall Execution Time Comparison', fontsize=12, fontweight='bold')
    ax1.grid(True, linestyle='--', alpha=0.6)
    ax1.legend(fontsize=10)

    ax2.plot(workload_sizes, orig_aging, marker='o', linewidth=2.5, color='#e67e22', label='Original Re-heapification Aging')
    ax2.plot(workload_sizes, red_aging, marker='s', linewidth=2.5, color='#2980b9', label='Redesigned Cohort Splicing Aging')
    ax2.set_xlabel('Workload Operations', fontsize=11, fontweight='bold')
    ax2.set_ylabel('Total Aging Sweep Time (ms)', fontsize=11, fontweight='bold')
    ax2.set_title('Dynamic Aging Sweep Overhead', fontsize=12, fontweight='bold')
    ax2.grid(True, linestyle='--', alpha=0.6)
    ax2.legend(fontsize=10)

    plt.tight_layout()
    plt.savefig('Graphs/Q7_original_vs_redesign_latency.png', dpi=300)
    plt.close()

print("[SUCCESS] All 5 high-resolution graphs generated in Graphs/ directory.")

