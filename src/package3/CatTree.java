package package3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CatTree implements Iterable<CatInfo> {
	public CatNode root;

	public CatTree(CatInfo c) {
		this.root = new CatNode(c);
	}

	private CatTree(CatNode c) {
		this.root = c;
	}

	public void addCat(CatInfo c) {
		this.root = root.addCat(new CatNode(c));
	}

	public void removeCat(CatInfo c) {
		this.root = root.removeCat(c);
	}

	public int mostSenior() {
		return root.mostSenior();
	}

	public int fluffiest() {
		return root.fluffiest();
	}

	public CatInfo fluffiestFromMonth(int month) {
		return root.fluffiestFromMonth(month);
	}

	public int hiredFromMonths(int monthMin, int monthMax) {
		return root.hiredFromMonths(monthMin, monthMax);
	}

	public int[] costPlanning(int nbMonths) {
		return root.costPlanning(nbMonths);
	}

	public Iterator<CatInfo> iterator() {
		return new CatTreeIterator();
	}

	class CatNode {

		CatInfo data;
		CatNode senior;
		CatNode same;
		CatNode junior;

		public CatNode(CatInfo data) {
			this.data = data;
			this.senior = null;
			this.same = null;
			this.junior = null;
		}

		public String toString() {
			String result = this.data.toString() + "\n";
			if (this.senior != null) {
				result += "more senior " + this.data.toString() + " :\n";
				result += this.senior.toString();
			}
			if (this.same != null) {
				result += "same seniority " + this.data.toString() + " :\n";
				result += this.same.toString();
			}
			if (this.junior != null) {
				result += "more junior " + this.data.toString() + " :\n";
				result += this.junior.toString();
			}
			return result;
		}

		public CatNode addCat(CatNode c) {
			CatNode current = this;

			// senior implementation
			// if the month hired for c is less than than the current month
			// c has been there for longer and is therefore the senior of current
			if (c.data.monthHired < current.data.monthHired) {
				if (current.senior == null) {
					current.senior = new CatNode(c.data);
				} else {
					current.senior.addCat(c);

				}

			}
			// junior implementation
			else if (c.data.monthHired > current.data.monthHired) {
				if (current.junior == null) {
					current.junior = new CatNode(c.data);

				} else {
					current.junior.addCat(c);
				}

			}
			// same implementation
			else if (c.data.monthHired == current.data.monthHired) {
				if (c.data.furThickness > current.data.furThickness) {

					CatNode tmp = new CatNode(current.data);
					tmp.same = current.same;
					current.data = c.data;
					current.same = tmp;

					return current;

				} else if (c.data.furThickness <= current.data.furThickness && current.same == null) {

					current.same = new CatNode(c.data); // this is good
				} else if (c.data.furThickness <= current.data.furThickness && current.same != null) {
					current.same.addCat(c);

				}
			}

			return this;

		}

		public CatNode removeCat(CatInfo c) {
			CatNode current = this;
			if (c.equals(current.data)) {

				if (current.same != null) {

					current.data = current.same.data;

					current.same = current.same.same;

				}

				else if (current.senior != null) {

					CatNode junior = current.junior;
					current.data = current.senior.data;
					current.same = current.senior.same;
					current.junior = current.senior.junior;
					current.senior = current.senior.senior;

					while (junior != null) {

						current.addCat(junior);
						if(junior.senior != null) {
							addCat(junior.senior);
						}
						if(junior.same != null) {
							addCat(junior.same);
						}
						junior = junior.junior;
						

					}
				} else if (current.junior != null) {

					current.data = current.junior.data;
					current.same = current.junior.same;
					current.senior = current.junior.senior;
					current.junior = current.junior.junior;

				} else {
					current = null;
				}

			} else {

				if (c.monthHired > current.data.monthHired) {
					if (current.junior != null) {
						current.junior = current.junior.removeCat(c);

					}

				} else if (c.monthHired < current.data.monthHired) {
					if (current.senior != null) {
						current.senior = current.senior.removeCat(c);

					}

				} else if (c.monthHired == current.data.monthHired) {

					if (c.furThickness <= current.data.furThickness) {
						if (current.same != null) {
							current.same = current.same.removeCat(c);

						}
					}
				}

			}
			return current;
		}

		public int mostSenior() {
			CatNode current = this;
			int mostS = 0;
			while (true) {
				if (current.senior != null) {
					current = current.senior;
				}
				if (current.senior == null) {
					mostS = current.data.monthHired;
					break;
				}

			}
			return mostS;

		}

		public int fluffiest() {
			CatNode current = this;
			int maxLeftValue;
			int maxRightValue;
			int fluffiest;
			int rootValue = current.data.furThickness;
			if (current.senior != null) {
				// recursion condition
				maxLeftValue = current.senior.fluffiest();
			} else {
				maxLeftValue = current.data.furThickness;
			}
			if (current.junior != null) {
				maxRightValue = current.junior.fluffiest();
			} else {
				maxRightValue = current.data.furThickness;
			}

			// compare
			if (maxLeftValue >= maxRightValue && maxLeftValue >= rootValue) {
				fluffiest = maxLeftValue;
			} else if (maxLeftValue <= maxRightValue && maxRightValue >= rootValue) {
				fluffiest = maxRightValue;
			} else {
				fluffiest = rootValue;
			}
			return fluffiest;
		}

		public int hiredFromMonths(int monthMin, int monthMax) {
			CatNode current = this;
			int count = 0;

			if (monthMin > monthMax) {
				return 0;
			}
			if (current.data.monthHired >= monthMin && current.data.monthHired <= monthMax) {

				count += 1;
			}
			// recursive
			if (current.senior != null) {
				count += current.senior.hiredFromMonths(monthMin, monthMax);
			}

			if (current.junior != null) {
				count += current.junior.hiredFromMonths(monthMin, monthMax);
			}

			if (current.same != null) {
				count += current.same.hiredFromMonths(monthMin, monthMax);
			}
			return count;

		}

		public CatInfo fluffiestFromMonth(int month) {
			CatNode current = this;
			if (current.data.monthHired == month) {
				return current.data;

			} else if (current.data.monthHired > month) {
				return current.senior.fluffiestFromMonth(month);
			} else {
				return current.junior.fluffiestFromMonth(month);
			}

		}

		public int[] costPlanning(int nbMonths) {
			int month = 243 + nbMonths;
			int[] array = new int[nbMonths];
			root = this;
			Iterator<CatInfo> it = iterator();

			while (it.hasNext()) {
				CatInfo info = (CatInfo) it.next();
				if (info.nextGroomingAppointment < month) {
					array[info.nextGroomingAppointment - 243] += info.expectedGroomingCost;
				}
			}

			return array;
		}

	}

	private class CatTreeIterator implements Iterator<CatInfo> {
		ArrayList<CatNode> aList = new ArrayList<CatNode>();

		public CatTreeIterator() {
			if (aList.isEmpty()) {
				inOrder(root);
			}

		}

		private void inOrder(CatNode r) {
			if (r == null) {
				return;
			}

			if (r.senior != null) {
				inOrder(r.senior);
			}
			if (r.same != null) {
				inOrder(r.same);
			}
			if (!aList.contains(r)) {
				aList.add(r);
			}
			if (r.junior != null) {
				inOrder(r.junior);
			}
		}

		public CatInfo next() {
			CatInfo next = aList.get(0).data;
			aList.remove(0);

			return next;
		}

		public boolean hasNext() {
			if (aList.isEmpty()) {
				return false;
			}
			return true;

		}

	}

}
