package ru.mail.polis;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Random;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    //    private Node root;
    class Node {
        Node(E value) {
            this.data = value;
        }

        E data;
        Node left;
        Node right;
        Node parent;

        @Override
        public String toString() {
            final StringBuilder str = new StringBuilder("N{");
            str.append("data=").append(data);
            if (left != null) {
                str.append(", left=").append(left);
            }
            if (right != null) {
                str.append(", right=").append(right);
            }
            str.append('}');
            return str.toString();
        }
    }

    private Node root;
    private int size;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("No first element, set is empty.");
        }
        Node current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.data;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("No last element, set is empty");
        }
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.data;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        System.out.println();
        return list;
    }

    private void inorderTraverse(Node current, List<E> list) {
        if (current != null) {
            inorderTraverse(current.left, list);
            list.add(current.data);
            inorderTraverse(current.right, list);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("Value is null.");
        }
        if (root != null) {
            Node current = root;
            while (current != null) {
                int cmp = compare(current.data, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    current = current.right;
                } else {
                    current = current.left;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        Node newItem = new Node(value);
        boolean res = RecursiveInsert(root, newItem);
        if(res) size++;
        return res;
    }

    private boolean RecursiveInsert(Node current, Node n) //current = p, n = q
    {
        boolean result;
        if (current == null) {
            root = n;
            result = true;
        } else {
            if (compare(n.data, current.data) < 0) //n < current
            {
                if (current.left == null) {
                    current.left = n;
                    current.left.parent = current;

                    balance_tree(current);
                    result = true;
                } else {
                    result = RecursiveInsert(current.left, n);
                }

                //current.left = RecursiveInsert(current.left, n);

            /* p        q
            if(p.left == null) {
                p.left = q;
                p.left.parent = p;

                checkBalance(p);
                result = true;
            } else {
                result = addAVLNode(p.left, q);
            }*/
            } else if (compare(n.data, current.data) > 0) //n > current
            {
                if (current.right == null) {
                    current.right = n;
                    current.right.parent = current;

                    balance_tree(current.right);
                    result = true;
                } else {
                    result = RecursiveInsert(current.right, n);
                }
            /*if(p.right == null) {
                p.right = q;
                p.right.parent = p;

                checkBalance(p.right);
                result = true;
            } else {
                result = addAVLNode(p.right, q);
            }
            current.right = RecursiveInsert(current.right, n);
            current = balance_tree(current);*/
            } else {result = false;}
        }
        return result;
        }

    private void balance_tree(Node current)
    {
        int b_factor = balance_factor(current);
        if (b_factor > 1)
        {
            if (getHeight(current.right.right) < getHeight(current.right.left)) {
                current = RotateRL(current);
            } else {
                current = RotateL(current);
            }
            //if (balance_factor(current.left) > 0) {current = RotateLL(current); }
            //else { current = RotateLR(current); }
        }
        else if (b_factor < -1)
        {
            if (getHeight(current.left.left) < getHeight(current.left.right)) {
                current = RotateLR(current);
            } else {
                current = RotateR(current);
            }
            //if (balance_factor(current.right) > 0) { current = RotateRL(current); }
            //else{ current = RotateRR(current); }
        }
        if (current.parent != null) {
            balance_tree(current.parent);
        } else {
            this.root = current;
        }
    }

    private int balance_factor(Node current)
    {
        return getHeight(current.right) - getHeight(current.left);
    }

    private int getHeight(Node current)
    {
        if (current == null) {
            return -1;
        }
        if (current.left == null && current.right == null) {
            return 0;
        } else if (current.left == null) {
            return 1 + getHeight(current.right);
        } else if (current.right == null) {
            return 1 + getHeight(current.left);
        } else {
            return 1 + Math.max(getHeight(current.left), getHeight(current.right));
        }
    }

    /*private int max(int l, int r)
    {
        return l > r ? l : r;
    }*/

    private Node RotateR(Node n)
    {
        Node v = n.left;
        n.left = v.right;

        if (v.right != null) {
            v.right.parent = n;
        }

        v.parent = n.parent;

        if (n != root) {
            if (n.parent.right == n) {
                n.parent.right = v;
            } else {
                n.parent.left = v;
            }
        }

        v.right = n;
        n.parent = v;

        return n;
    }
    private Node RotateL(Node n)
    {
        Node v = n.right;
        n.right = v.left;

        if (v.left != null) {
            v.left.parent = n;
        }
        v.parent = n.parent;
        if (n != root) {
            if (n.parent.left == n) {
                n.parent.left = v;
            } else {
                n.parent.right = v;
            }
        }
        v.left = n;
        n.parent = v;

        return n;
    }
    private Node RotateLR(Node n)
    {
        n.left = RotateL(n.left);
        return RotateR(n);
    }
    private Node RotateRL(Node n)
    {
        n.right = RotateR(n.right);
        return RotateL(n);
    }

    @Override
    public boolean remove(E value) {
        //root = Delete(root, value);
        //return false;
        int sizePrev = size();
        root =  Delete(this.root, value);
        if (sizePrev != size())
            return true;
        else
            return false;
    }

    private Node Delete(Node current, E target)
    {
        Node parent;
        if (current == null)
        { throw new NullPointerException("value is null"); }
        else
        {
            //left subtree
            if (compare(target, current.data) < 0)
            {
                current.left = Delete(current.left, target);
                if (balance_factor(current) == -2)//here
                {
                    if (balance_factor(current.right) <= 0)
                    {
                        current = RotateR(current);
                    }
                    else
                    {
                        current = RotateRL(current);
                    }
                }
            }
            //right subtree
            else if (compare(target,  current.data) > 0)
            {
                current.right = Delete(current.right, target);
                if (balance_factor(current) == 2)
                {
                    if (balance_factor(current.left) >= 0)
                    {
                        current = RotateL(current);
                    }
                    else
                    {
                        current = RotateLR(current);
                    }
                }
            }
            //if target is found
            else
            {
                if (current.right != null)
                {
                    parent = current.right;
                    while (parent.left != null)
                    {
                        parent = parent.left;
                    }
                    current.data = parent.data;
                    current.right = Delete(current.right, parent.data);

                    if (balance_factor(current) == 2)//rebalancing
                    {
                        if (balance_factor(current.left) >= 0)
                        {
                            current = RotateL(current);
                        }
                        else { current = RotateLR(current); }
                    }

                }
                else
                {   //if current.left != null
                    size--;
                    return current.left;
                }
            }
        }
        return current;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public String toString() {
        return "Tree{" + root + "}";
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.add(10);

        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(5);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.add(15);
        System.out.println(tree.size);
        System.out.println(tree);

        System.out.println("------------");
        Random rnd = new Random();
        tree = new AVLTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        /*tree = new AVLTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });*/
        tree = new AVLTree<>();
        for (int i = 0; i < 30; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        ISortedSet<Integer> set = new AVLTree<Integer>();
        for (int i = 0; i < 1000; i++) {
            set.add(rnd.nextInt(1000));
        }
        for (int i = 0; i < 1000; i++) {
            set.remove(rnd.nextInt(1000));
        }
    }
}
