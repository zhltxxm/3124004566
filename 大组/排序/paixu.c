/**
 * 排序算法实现与测试（C语言单文件版）
 * 包含：插入排序、归并排序、快速排序、计数排序、基数排序
 * 功能：性能测试、文件数据生成与排序
 * 编译：gcc -o sort_demo sort_demo.c
 * 运行：./sort_demo
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

// 辅助函数声明
void swap(int *a, int *b);
void printArray(int arr[], int n);
int* generateRandomData(int size);

// 排序算法声明
void InsertSort(int arr[], int n);
void MergeSort(int arr[], int left, int right);
void Merge(int arr[], int left, int mid, int right);
void QuickSort(int arr[], int low, int high);
int Partition(int arr[], int low, int high);
void CountSort(int arr[], int n);
void RadixCountSort(int arr[], int n);

// 测试与文件函数声明
void TestSortTime(void (*sortFunc)(int[], int), int data[], int size, char* name);
void TestLargeData();
void TestSmallData();
void GenerateDataToFile(char* filename, int size);
void ReadAndSort(char* filename, void (*sortFunc)(int[], int));

// ------------------- 辅助函数实现 -------------------
void swap(int *a, int *b) {
    int temp = *a;
    *a = *b;
    *b = temp;
}

void printArray(int arr[], int n) {
    for (int i = 0; i < n; i++) printf("%d ", arr[i]);
    printf("\n");
}

int* generateRandomData(int size) {
    int *data = (int *)malloc(size * sizeof(int));
    for (int i = 0; i < size; i++) data[i] = rand() % 1000;
    return data;
}

// ------------------- 排序算法实现 -------------------
// 插入排序
void InsertSort(int arr[], int n) {
    for (int i = 1; i < n; i++) {
        int key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}

// 归并排序
void Merge(int arr[], int left, int mid, int right) {
    int n1 = mid - left + 1;
    int n2 = right - mid;
    int L[n1], R[n2];
    for (int i = 0; i < n1; i++) L[i] = arr[left + i];
    for (int j = 0; j < n2; j++) R[j] = arr[mid + 1 + j];
    int i = 0, j = 0, k = left;
    while (i < n1 && j < n2) {
        if (L[i] <= R[j]) arr[k++] = L[i++];
        else arr[k++] = R[j++];
    }
    while (i < n1) arr[k++] = L[i++];
    while (j < n2) arr[k++] = R[j++];
}

void MergeSort(int arr[], int left, int right) {
    if (left < right) {
        int mid = left + (right - left) / 2;
        MergeSort(arr, left, mid);
        MergeSort(arr, mid + 1, right);
        Merge(arr, left, mid, right);
    }
}

// 快速排序
int Partition(int arr[], int low, int high) {
    int pivot = arr[(low + high) / 2];
    while (low <= high) {
        while (arr[low] < pivot) low++;
        while (arr[high] > pivot) high--;
        if (low <= high) {
            swap(&arr[low], &arr[high]);
            low++;
            high--;
        }
    }
    return low;
}

void QuickSort(int arr[], int low, int high) {
    if (low < high) {
        int pi = Partition(arr, low, high);
        QuickSort(arr, low, pi - 1);
        QuickSort(arr, pi, high);
    }
}

// 计数排序
void CountSort(int arr[], int n) {
    int max = arr[0], min = arr[0];
    for (int i = 1; i < n; i++) {
        if (arr[i] > max) max = arr[i];
        if (arr[i] < min) min = arr[i];
    }
    int range = max - min + 1;
    int *count = (int *)calloc(range, sizeof(int));
    for (int i = 0; i < n; i++) count[arr[i] - min]++;
    int idx = 0;
    for (int i = 0; i < range; i++) {
        while (count[i]-- > 0) arr[idx++] = i + min;
    }
    free(count);
}

// 基数排序
void RadixCountSort(int arr[], int n) {
    int max = arr[0];
    for (int i = 1; i < n; i++) if (arr[i] > max) max = arr[i];
    for (int exp = 1; max / exp > 0; exp *= 10) {
        int output[n];
        int count[10] = {0};
        for (int i = 0; i < n; i++) count[(arr[i] / exp) % 10]++;
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            output[count[(arr[i]/exp)%10] - 1] =arr[i];
            count[(arr[i]/exp)%10]--;
    	}
        for (int i = 0; i < n; i++) arr[i] = output[i];
    }
}

// ------------------- 测试与文件函数 -------------------
void TestSortTime(void (*sortFunc)(int[], int), int data[], int size, char* name) {
    clock_t start = clock();
    sortFunc(data, size);
    clock_t end = clock();
    printf("[%s] Size: %d, Time: %.5f sec\n", name, size, (double)(end - start)/CLOCKS_PER_SEC);
}

void TestLargeData() {
    int sizes[] = {10000, 50000, 200000};
    for (int i = 0; i < 3; i++) {
        int size = sizes[i];
        int *data = generateRandomData(size);
        printf("\n----- Testing Size: %d -----\n", size);
        TestSortTime(InsertSort, data, size, "InsertSort");
        free(data);
    }
}

void TestSmallData() {
    int smallData[100];
    clock_t start = clock();
    for (int i = 0; i < 100000; i++) {
        for (int j = 0; j < 100; j++) smallData[j] = rand() % 1000;
        InsertSort(smallData, 100);
    }
    clock_t end = clock();
    printf("[SmallData] 100k repeats, Time: %.5f sec\n", (double)(end - start)/CLOCKS_PER_SEC);
}

void GenerateDataToFile(char* filename, int size) {
    FILE *fp = fopen(filename, "w");
    if (!fp) return;
    fprintf(fp, "%d\n", size);
    for (int i = 0; i < size; i++) fprintf(fp, "%d ", rand() % 1000);
    fclose(fp);
    printf("Generated data saved to %s\n", filename);
}

void ReadAndSort(char* filename, void (*sortFunc)(int[], int)) {
    FILE *fp = fopen(filename, "r");
    if (!fp) return;
    int size;
    fscanf(fp, "%d", &size);
    int *data = (int *)malloc(size * sizeof(int));
    for (int i = 0; i < size; i++) fscanf(fp, "%d", &data[i]);
    fclose(fp);
    clock_t start = clock();
    sortFunc(data, size);
    clock_t end = clock();
    printf("[FileSort] %s, Size: %d, Time: %.5f sec\n", filename, size, (double)(end - start)/CLOCKS_PER_SEC);
    free(data);
}

// ------------------- 主程序 -------------------
int main() {
    srand(time(NULL));

    printf("===== Large Data Test =====\n");
    TestLargeData();

    printf("\n===== Small Data Test =====\n");
    TestSmallData();

    printf("\n===== File IO Test =====\n");
    GenerateDataToFile("test_data.txt", 5000);
    //ReadAndSort("test_data.txt", QuickSort);

    return 0;
}