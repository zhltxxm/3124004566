#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

// 定义链栈节点结构
typedef struct StackNode {
    double value;
    struct StackNode *next;
} StackNode;

// 定义链栈结构
typedef struct LinkedStack {
    StackNode *top;
} LinkedStack;

// 初始化链栈
void initStack(LinkedStack *stack) {
    stack->top = NULL;
}

// 判断栈是否为空
bool isEmpty(LinkedStack *stack) {
    return stack->top == NULL;
}

// 入栈操作
void push(LinkedStack *stack, double value) {
    StackNode *newNode = (StackNode *)malloc(sizeof(StackNode));
    newNode->value = value;
    newNode->next = stack->top;
    stack->top = newNode;
}

// 出栈操作
double pop(LinkedStack *stack) {
    if (isEmpty(stack)) {
        printf("Stack is empty, cannot pop.\n");
        exit(EXIT_FAILURE);
    }
    StackNode *temp = stack->top;
    double value = temp->value;
    stack->top = temp->next;
    free(temp);
    return value;
}

// 查看栈顶元素
double peek(LinkedStack *stack) {
    if (isEmpty(stack)) {
        printf("Stack is empty, cannot peek.\n");
        exit(EXIT_FAILURE);
    }
    return stack->top->value;
}

// 获取运算符优先级
int precedence(char op) {
    if (op == '+' || op == '-')
        return 1;
    if (op == '*' || op == '/')
        return 2;
    return 0;
}

// 应用运算符进行计算
void applyOperator(LinkedStack *operands, LinkedStack *operators) {
    double right = pop(operands);
    double left = pop(operands);
    char op = (char)pop(operators);
    switch (op) {
        case '+':
            push(operands, left + right);
            break;
        case '-':
            push(operands, left - right);
            break;
        case '*':
            push(operands, left * right);
            break;
        case '/':
            if (right == 0) {
                printf("Division by zero error.\n");
                exit(EXIT_FAILURE);
            }
            push(operands, left / right);
            break;
    }
}

// 计算表达式的值
double evaluateExpression(char *expression) {
    LinkedStack operands;
    LinkedStack operators;
    initStack(&operands);
    initStack(&operators);
    int i = 0;
    int len = strlen(expression);
    while (i < len) {
        if (expression[i] >= '0' && expression[i] <= '9') {
            double num = 0;
            int decimal = 0;
            double decimalFactor = 0.1;
            while (i < len && (expression[i] >= '0' && expression[i] <= '9' || expression[i] == '.')) {
                if (expression[i] == '.') {
                    decimal = 1;
                } else if (decimal == 0) {
                    num = num * 10 + (expression[i] - '0');
                } else {
                    num += (expression[i] - '0') * decimalFactor;
                    decimalFactor /= 10;
                }
                i++;
            }
            i--;
            push(&operands, num);
        } else if (expression[i] == '(') {
            push(&operators, (double)'(');
        } else if (expression[i] == ')') {
            while (peek(&operators) != (double)'(') {
                applyOperator(&operands, &operators);
            }
            pop(&operators); // 弹出 '('
        } else if (expression[i] == '+' || expression[i] == '-' || expression[i] == '*' || expression[i] == '/') {
            while (!isEmpty(&operators) && peek(&operators) != (double)'(' && precedence((char)peek(&operators)) >= precedence(expression[i])) {
                applyOperator(&operands, &operators);
            }
            push(&operators, (double)expression[i]);
        }
        i++;
    }
    while (!isEmpty(&operators)) {
        applyOperator(&operands, &operators);
    }
    return pop(&operands);
}

int main() {
    char expression[100];
    while (1) {
        printf("请输入四则运算表达式（支持括号，例如：(3+2)*5/2 ）：");
        fgets(expression, sizeof(expression), stdin);
        // 去除fgets读取的换行符
        expression[strcspn(expression, "\n")] = '\0';
        if (expression[0] == '\0') {
            printf("输入不能为空，请重新输入。\n");
            continue;
        }
        double result = evaluateExpression(expression);
        printf("表达式 %s 的结果是: %lf\n", expression, result);
        break;
    }
    return 0;
}