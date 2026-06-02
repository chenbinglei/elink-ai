/**
 * @typedef {Object} TreeFlagItem
 * @property {string} id
 * @property {string} parentId
 *
 * @typedef {Object} TreeNode
 * @property {string} id
 * @property {string} parentId
 * @property {Array<TreeNode>} children
 * 将平级树数据转化为树形数据
 * @param {Array<TreeFlagItem>} treeFlatData
 * @returns {Array<TreeNode>} treeData
 */
export const transformTreeData = (treeFlatData) => {
  const treeMap = new Map();

  // Create a map of all nodes
  treeFlatData.forEach((item) => {
    treeMap.set(item.id, { ...item, children: [] });
  });

  const tree = [];

  // Build the tree structure
  treeFlatData.forEach((item) => {
    const node = treeMap.get(item.id);
    if (item.parentId && treeMap.has(item.parentId)) {
      treeMap.get(item.parentId).children.push(node);
    } else {
      tree.push(node);
    }
  });

  return tree;
};

/**
 * 将宽度转化为vw
 * @description 1920px = 100vw
 * @param {Number | String} width
 * @returns
 */
export const transformWidthToVW = (width) => {
  return `${Number(100 * (+width / 1920)).toFixed(2)}vw`;
};

export const transformTableCellText = (row, column) => {
  const { formatter, key } = column;
  const val = row[key];
  if (formatter && typeof formatter === "function") {
    return formatter(val, row);
  }
  return val ?? "--";
};
