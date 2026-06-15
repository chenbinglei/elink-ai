interface TreeFlagItem {
  id: string;
  parentId: string;
  [key: string]: unknown;
}

interface TreeNode extends TreeFlagItem {
  children: TreeNode[];
}

/**
 * 将平级树数据转化为树形数据
 */
export const transformTreeData = (treeFlatData: TreeFlagItem[]): TreeNode[] => {
  const treeMap = new Map<string, TreeNode>();

  treeFlatData.forEach((item) => {
    treeMap.set(item.id, { ...item, children: [] });
  });

  const tree: TreeNode[] = [];

  treeFlatData.forEach((item) => {
    const node = treeMap.get(item.id)!;
    if (item.parentId && treeMap.has(item.parentId)) {
      treeMap.get(item.parentId)!.children.push(node);
    } else {
      tree.push(node);
    }
  });

  return tree;
};

/**
 * 将宽度转化为vw
 * @description 1920px = 100vw
 */
export const transformWidthToVW = (width: number | string): string => {
  return `${Number(100 * (+width / 1920)).toFixed(2)}vw`;
};

export const transformTableCellText = (row: Record<string, unknown>, column: { formatter?: (val: unknown, row: Record<string, unknown>) => string; key: string }): string => {
  const { formatter, key } = column;
  const val = row[key];
  if (formatter && typeof formatter === "function") {
    return formatter(val, row);
  }
  return (val ?? "--") as string;
};
