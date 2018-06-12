package com.ruomm.base.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * 项目名称：工具类 类名称：ListUtils 类描述： List工具类，可用于List常用操作 创建人：王龙能 创建时间：2014-3-13 上午11:25:07 修改人：王龙能
 * 修改时间：2014-3-13 上午11:25:07 修改备注：
 *
 * @version
 */
public class ListUtils {

	/** 默认的分隔符r **/
	public static final String DEFAULT_JOIN_SEPARATOR = ",";

	/**
	 * 获取列表的大小
	 *
	 * <pre>
	 * getSize(null)   =   0;
	 * getSize({})     =   0;
	 * getSize({1})    =   1;
	 * </pre>
	 *
	 * @param <V>
	 * @param sourceList
	 * @return 如果列表为null或空，则返回0，否则返回，则为list.size（） 。
	 */
	public static <V> int getSize(List<V> sourceList) {
		return sourceList == null ? 0 : sourceList.size();
	}

	/**
	 * 为空或它的大小为0
	 *
	 * <pre>
	 * isEmpty(null)   =   true;
	 * isEmpty({})     =   true;
	 * isEmpty({1})    =   false;
	 * </pre>
	 *
	 * @param <V>
	 * @param sourceList
	 * @return 如果列表为空或它的大小为0，返回真，否则返回假。
	 */
	public static <V> boolean isEmpty(List<V> sourceList) {
		return sourceList == null || sourceList.size() == 0;
	}

	/**
	 * 比较两个表
	 *
	 * <pre>
	 * isEquals(null, null) = true;
	 * isEquals(new ArrayList&lt;String&gt;(), null) = false;
	 * isEquals(null, new ArrayList&lt;String&gt;()) = false;
	 * isEquals(new ArrayList&lt;String&gt;(), new ArrayList&lt;String&gt;()) = true;
	 * </pre>
	 *
	 * @param <V>
	 * @param actual
	 * @param expected
	 * @return
	 */
	public static <V> boolean isEquals(ArrayList<V> actual, ArrayList<V> expected) {
		if (actual == null) {
			return expected == null;
		}
		if (expected == null) {
			return false;
		}
		if (actual.size() != expected.size()) {
			return false;
		}

		for (int i = 0; i < actual.size(); i++) {
			if (!ObjectUtils.isEquals(actual.get(i), expected.get(i))) {
				return false;
			}
		}
		return true;
	}


	/**
	 * 添加不同的条目列表
	 *
	 * @param <V>
	 * @param sourceList
	 * @param entry
	 * @return 如果条目SOURCELIST已经存在，则返回false，否则将其添加并返回true。
	 */
	public static <V> boolean addDistinctEntry(List<V> sourceList, V entry) {
		return sourceList != null && !sourceList.contains(entry) ? sourceList.add(entry) : false;
	}

	/**
	 * 添加所有不同的条目从列表2至LIST1
	 *
	 * @param <V>
	 * @param sourceList
	 * @param entryList
	 * @return 条目的计数被添加
	 */
	public static <V> int addDistinctList(List<V> sourceList, List<V> entryList) {
		if (sourceList == null || isEmpty(entryList)) {
			return 0;
		}

		int sourceCount = sourceList.size();
		for (V entry : entryList) {
			if (!sourceList.contains(entry)) {
				sourceList.add(entry);
			}
		}
		return sourceList.size() - sourceCount;
	}

	/**
	 * 在列表中删除重复的条目
	 *
	 * @param <V>
	 * @param sourceList
	 * @return t条目的计数被删除
	 */
	public static <V> int distinctList(List<V> sourceList) {
		if (isEmpty(sourceList)) {
			return 0;
		}

		int sourceCount = sourceList.size();
		int sourceListSize = sourceList.size();
		for (int i = 0; i < sourceListSize; i++) {
			for (int j = i + 1; j < sourceListSize; j++) {
				if (sourceList.get(i).equals(sourceList.get(j))) {
					sourceList.remove(j);
					sourceListSize = sourceList.size();
					j--;
				}
			}
		}
		return sourceCount - sourceList.size();
	}

	/**
	 * 添加NOT NULL条目列出
	 *
	 * @param sourceList
	 * @param value
	 * @return <ul>
	 *         <li>如果SOURCELIST为null，则返回false</li>
	 *         <li>如果值为null，返回false</li>
	 *         <li>返回将对List.Add（对象）</li>
	 *         </ul>
	 */
	public static <V> boolean addNotNullValue(List<V> sourceList, V value) {
		return sourceList != null && value != null ? sourceList.add(value) : false;
	}

	/**
	 * 倒置列表
	 *
	 * @param <V>
	 * @param sourceList
	 * @return
	 */
	public static <V> List<V> invertList(List<V> sourceList) {
		if (isEmpty(sourceList)) {
			return sourceList;
		}

		List<V> invertList = new ArrayList<V>(sourceList.size());
		for (int i = sourceList.size() - 1; i >= 0; i--) {
			invertList.add(sourceList.get(i));
		}
		return invertList;
	}

	/**
	 * List的分页显示
	 */
	public static <V> int getListPageSize(List<V> sourceList, int perPageSize) {

		int sizeList = getSize(sourceList);
		if (sizeList % perPageSize == 0) {
			return sizeList / perPageSize;
		}
		else {
			return sizeList / perPageSize + 1;
		}
	}

	public static <V> List<V> getPageSpitList(List<V> sourceList, int pageIndex, int perPageSize) {
		int sizeList = getSize(sourceList);
		int indexStart = pageIndex * perPageSize;
		int indexEnd = (pageIndex + 1) * perPageSize;
		if (indexStart >= sizeList) {
			return null;
		}
		else {
			if (indexEnd > sizeList) {
				indexEnd = sizeList;
			}
			List<V> listTemp = new ArrayList<V>();
			listTemp.addAll(sourceList.subList(indexStart, indexEnd));
			if (isEmpty(sourceList)) {
				return null;
			}
			else {
				return listTemp;
			}
		}
	}

	/**
	 * Array数据转换成字符串拼接
	 * @param list
	 * @param separate
	 * @return
	 */
	public static String array2String(List<String> list, String separate) {
		StringBuilder sb = new StringBuilder();
		if (list != null) {
			int size = list.size();
			for (int i = 0; i < size; i++) {
				sb.append(list.get(i)).append(separate);
			}
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		}
		return null;
	}
	/**
	 * Array数据克隆复制
	 * @param array
	 * @return
	 */
	public static Object[] clone(Object[] array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	/**
	 * @Title: deepCopy
	 * @Description: 通过序列化实现元素深拷贝，集合元素要可序列化
	 * @param src
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 *             List
	 * @throws
	 */
	public static <T extends Serializable> List<T> clone(List<T> ls) throws IOException, ClassNotFoundException {
		if (isListEmpty(ls)) {
			return null;
		}
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(ls);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		@SuppressWarnings("unchecked")
		List<T> dest = (List<T>) in.readObject();
		return dest;
	}
	/**
	 * 合并Array数据
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static Object[] addAll(Object[] array1, Object[] array2) {
		if (isEmpty(array1)) {
			return clone(array2);
		}
		if (isEmpty(array2)) {
			return clone(array1);
		}
		Object[] joinedArray = (Object[]) Array.newInstance(array1.getClass().getComponentType(), array1.length
				+ array2.length);

		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}
	/**
	 * List数据是否为空
	 * @param list
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isListEmpty(List list) {
		if (null == list || list.size() == 0) {
			return true;
		}
		return false;
	}
	/**
	 * Array数据是否为空
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(Object[] array) {
		if (array == null || array.length == 0 || array.length == 1 && null == array[0]) {
			return true;
		}
		return false;
	}

	/**
	 * @Title: reverse
	 * @Description: 把集合的元素排序反转
	 * @param ls
	 *            要反转的集合
	 * @return List<T>
	 * @throws
	 */
	public static <T> List<T> reverse(List<T> ls) {
		List<T> nls = new ArrayList<T>();
		for (T t : ls) {
			nls.add(0, t);
		}
		return nls;
	}

	/**
	 * edit by zg
	 *
	 * @Title: getSubList
	 * @Description: 从一个父集合中取第几页元素。如果最后一页元素个数大于0又不足一页，则从头部取过来补足一页。如果页数大于集合页数，则返回 空集合
	 * @param pls
	 *            parentlist父集合
	 * @param pn
	 *            pagenum 页码
	 * @param np
	 *            numperpage 每页包含几个元素
	 * @return List<T>
	 * @throws
	 */
	public static <T> List<T> getSubList(List<T> pls, int pn, int np) {
		List<T> sls = new ArrayList<T>();
		if (!isListEmpty(pls)) {
			if (pls.size() % np > 0 && pn - 1 > pls.size() / np) {// 已经超过集合范围
				return sls;
			}
			if (pls.size() % np == 0 && pn > pls.size() / np) {
				return sls;
			}
			int startPos = (pn - 1) * np;
			for (int i = 0; i < np; i++) {
				int tempPos = startPos + i;
				if (tempPos >= pls.size()) {
					sls.add(pls.get(tempPos % pls.size()));
				}
				else {
					sls.add(pls.get(tempPos));
				}
			}
		}
		return sls;
	}
}
