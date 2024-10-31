package livewallpaper.aod.screenlock.lib.UgameLib.Shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import livewallpaper.aod.screenlock.lib.UgameLib.Animations.Deplace;
import livewallpaper.aod.screenlock.lib.UgameLib.Animations.Fade;
import livewallpaper.aod.screenlock.lib.UgameLib.Animations.Rotation;
import livewallpaper.aod.screenlock.lib.UgameLib.Animations.Sizer;

public class Urect {
    public static List<Urect> list = new ArrayList();
    List<ClickDownListner> ClickDownlisteners;
    List<ClickUpListner> ClickUplisteners;
    public boolean Clicked;
    public boolean DrawChilds;
    List<TouchMoveListner> TouchMovelisteners;
    List<UpdateListner> UpdateListners;
    protected double alpha;
    private List<Urect> childrens;
    public int color;
    Deplace deplace;
    Fade fade;
    protected double height;
    public Paint paint;
    private Urect parent;
    protected double radius;
    protected double rotate;
    Rotation rotation;
    Sizer sizer;
    public double skewX;
    public double skewY;
    protected double width;
    protected double x;
    protected double y;

    public interface ClickDownListner {
        void OnClickDownDo(double d, double d2);
    }

    public interface ClickUpListner {
        void OnClickUpDo(double d, double d2);
    }

    public interface TouchMoveListner {
        void OnMoveDo(Urect urect, double d, double d2);
    }

    public interface UpdateListner {
        void Update(Urect urect);
    }

    public static boolean isBetween(double d, double d2, double d3) {
        if (d < d2 || d > d3) {
            return d >= d3 && d <= d2;
        }
        return true;
    }

    public Fade getFade() {
        return this.fade;
    }

    public void setFadeAnnimation(Fade fade2) {
        if (this.fade != null) {
            this.fade.Remove();
        }
        this.fade = fade2;
    }

    public Deplace getDeplace() {
        return this.deplace;
    }

    public void setDeplaceAnnimation(Deplace deplace2) {
        if (this.deplace != null) {
            this.deplace.remove();
        }
        this.deplace = deplace2;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public void setRotationAnimation(Rotation rotation2) {
        this.rotation = rotation2;
    }

    public Sizer getSizer() {
        return this.sizer;
    }

    public void setSizerAnnimation(Sizer sizer2) {
        this.sizer = sizer2;
    }

    public Urect(double d, double d2, double d3, double d4, int i) {
        this(d, d2, d3, d4);
        this.paint.setColor(i);
        this.color = i;
    }

    public Urect(double d, double d2, double d3, double d4) {
        this.DrawChilds = true;
        this.childrens = new ArrayList();
        this.Clicked = false;
        if (list == null) {
            list = new ArrayList();
        }
        this.x = d;
        this.y = d2;
        this.height = d4;
        this.width = d3;
        this.paint = new Paint();
        setAlpha(255.0d);
        this.paint.setColor(0);
        this.color = 0;
        list.add(this);
    }

    public Urect(double d, double d2, double d3, double d4, boolean z) {
        this.DrawChilds = true;
        this.childrens = new ArrayList();
        this.Clicked = false;
        this.x = d;
        this.y = d2;
        this.height = d4;
        this.width = d3;
        this.paint = new Paint();
        setAlpha(255.0d);
        this.paint.setColor(0);
        this.color = 0;
        if (z) {
            list.add(this);
        }
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double d) {
        this.radius = d;
    }

    public double getLeft() {
        return this.parent == null ? this.x : this.parent.getLeft() + this.x;
    }

    public double getRelativeLeft() {
        return this.x;
    }

    public void setLeft(double d) {
        this.x = d;
    }

    public double getTop() {
        return this.parent == null ? this.y : this.parent.getTop() + this.y;
    }

    public double getRelativeTop() {
        return this.y;
    }

    public void setTop(double d) {
        this.y = d;
    }

    public double getRight() {
        return (this.parent == null ? getRelativeLeft() : this.parent.getLeft() + getRelativeLeft()) + Width();
    }

    public double getRelativeRight() {
        return this.x + Width();
    }

    public void setRight(double d) {
        setLeft(d - Width());
    }

    public double getBottom() {
        return (this.parent == null ? this.y : this.parent.getTop() + this.y) + Height();
    }

    public double getRelativeBottom() {
        return this.y + Height();
    }

    public void setBottom(double d) {
        setTop(d - Height());
    }

    public double Height() {
        return this.height;
    }

    public void setHeight(double d) {
        this.height = d;
    }

    public double Width() {
        return this.width;
    }

    public void setWidth(double d) {
        this.width = d;
    }

    public double getRotate() {
        return this.rotate;
    }

    public void setRotate(double d) {
        this.rotate = d;
    }

    public double getAlpha() {
        if (this.parent != null) {
            return (this.alpha / 255.0d) * this.parent.getAlpha();
        }
        return this.alpha;
    }

    public double getRelativeAlpha() {
        return this.alpha;
    }

    public void setAlpha(double d) {
        this.alpha = d;
    }

    public int getColor() {
        return this.paint.getColor();
    }

    public void setColor(int i) {
        this.paint.setColor(i);
        this.color = i;
    }

    public List<Urect> getChildrens() {
        return this.childrens;
    }

    public void setChildrenCollection(List<Urect> list2) {
        this.childrens = list2;
    }

    public boolean removeParent() {
        if (this.parent == null) {
            return false;
        }
        this.parent = null;
        return true;
    }

    public void AddChild(Urect urect) {
        if (this.childrens == null) {
            this.childrens = new ArrayList();
        }
        this.childrens.add(urect);
        urect.parent = this;
    }

    public boolean DeleteChild(Urect urect) {
        if (urect.getParent() != this) {
            return false;
        }
        if (this.childrens != null) {
            this.childrens.remove(urect);
        }
        return urect.removeParent();
    }

    public Urect getParent() {
        return this.parent;
    }

    public void setParent(Urect urect) {
        if (urect != null) {
            urect.getChildrens().remove(this);
        }
        this.parent = urect;
        urect.AddChild(this);
    }

    public Rect GetRect() {
        return new Rect((int) getLeft(), (int) getTop(), (int) getRight(), (int) getBottom());
    }

    public RectF GetRectF() {
        return new RectF((float) getLeft(), (float) getTop(), (float) getRight(), (float) getBottom());
    }

    public double GetCenterX() {
        return (getLeft() + getRight()) / 2.0d;
    }

    public double getCenterY() {
        return (getTop() + getBottom()) / 2.0d;
    }

    public void Draw(Canvas canvas) {
        try {
            int save = canvas.save();
            canvas.rotate((float) ((int) getRotate()), (float) ((int) GetCenterX()), (float) ((int) getCenterY()));
            canvas.skew((float) ((int) this.skewX), (float) ((int) this.skewY));
            this.paint.setAlpha((int) getAlpha());
            if (this.color != 0) {
                if (getRadius() == 0.0d) {
                    canvas.drawRect(GetRect(), this.paint);
                } else {
                    canvas.drawRoundRect(GetRectF(), (float) this.radius, (float) this.radius, this.paint);
                }
            }
            canvas.restoreToCount(save);
            drawChildrens(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    public void drawChildrens(Canvas canvas) {
        if (this.DrawChilds && this.childrens != null) {
            int i = 0;
            while (i < this.childrens.size()) {
                try {
                    if (this.childrens.get(i) != null) {
                        this.childrens.get(i).Draw(canvas);
                    }
                    i++;
                } catch (Exception unused) {
                    return;
                }
            }
        }
    }

    public boolean IsClicked(double d, double d2) {
        return d >= getLeft() && d <= getRight() && d2 >= getTop() && d2 <= getBottom();
    }

    public void addOnClickDownListner(ClickDownListner clickDownListner) {
        if (this.ClickDownlisteners == null) {
            this.ClickDownlisteners = new ArrayList();
        }
        this.ClickDownlisteners.add(clickDownListner);
    }

    public void addOnClickUpListner(ClickUpListner clickUpListner) {
        if (this.ClickUplisteners == null) {
            this.ClickUplisteners = new ArrayList();
        }
        this.ClickUplisteners.add(clickUpListner);
    }

    public void addOnTouchMoveListner(TouchMoveListner touchMoveListner) {
        if (this.TouchMovelisteners == null) {
            this.TouchMovelisteners = new ArrayList();
        }
        this.TouchMovelisteners.add(touchMoveListner);
    }

    public void OnUpdateListner(UpdateListner updateListner) {
        if (this.UpdateListners == null) {
            this.UpdateListners = new ArrayList();
        }
        this.UpdateListners.add(updateListner);
    }

    public void removeOnClickDownListner(ClickDownListner clickDownListner) {
        if (this.ClickDownlisteners != null) {
            this.ClickDownlisteners.remove(clickDownListner);
        }
    }

    public void removeOnClickUpListner(ClickUpListner clickUpListner) {
        if (this.ClickUplisteners != null) {
            this.ClickUplisteners.remove(clickUpListner);
        }
    }

    public void removeOnTouchMoveListner(TouchMoveListner touchMoveListner) {
        if (this.TouchMovelisteners != null) {
            this.TouchMovelisteners.remove(touchMoveListner);
        }
    }

    public void removeUpdateListner(UpdateListner updateListner) {
        this.UpdateListners.remove(updateListner);
    }

    public static void CheckRectsClickUp() {
        if(list!=null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                try {
                    list.get(size).checkClickUp();
                } catch (Exception unused) {
                }
            }
        }
    }

    public static void CheckRectTouchMove(double d, double d2) {
        if(list!=null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                try {
                    list.get(size).checkTouchMove(d, d2);
                } catch (Exception unused) {
                }
            }
        }
    }

    public static void CheckUpdates() {
        if(list!=null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                try {
                    list.get(size).CheckObjUpdates();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean checkClickDown(double d, double d2) {
        if(childrens!=null) {
            for (int size = this.childrens.size() - 1; size >= 0; size--) {
                if (this.childrens.get(size).checkClickDown(d, d2)) {
                    return true;
                }
            }
            int i = 0;
            if (!IsClicked(d, d2) || this.ClickDownlisteners == null) {
                return false;
            }
            boolean z = false;
            while (i < this.ClickDownlisteners.size()) {
                this.ClickDownlisteners.get(i).OnClickDownDo(d, d2);
                i++;
                z = true;
            }
            this.Clicked = true;
            return z;
        }
        return false;
    }

    public void checkClickUp() {
        if(ClickUplisteners!=null) {
            if (this.Clicked && this.ClickUplisteners != null) {
                for (int i = 0; i < this.ClickUplisteners.size(); i++) {
                    this.ClickUplisteners.get(i).OnClickUpDo(this.x, this.y);
                }
                this.Clicked = false;
            }
        }
    }

    public void checkTouchMove(double d, double d2) {

        if(TouchMovelisteners!=null) {
            if (this.TouchMovelisteners != null) {
                for (int i = 0; i < this.TouchMovelisteners.size(); i++) {
                    this.TouchMovelisteners.get(i).OnMoveDo(this, d, d2);
                }
            }
        }
    }

    public void CheckObjUpdates() {

            if (this.UpdateListners != null) {
                for (int size = this.UpdateListners.size() - 1; size >= 0; size--) {
                    this.UpdateListners.get(size).Update(this);
                }
            }
            if (this.childrens != null) {
                for (int size2 = this.childrens.size() - 1; size2 >= 0; size2--) {
                    this.childrens.get(size2).CheckObjUpdates();
                }
            }
    }

    public boolean isCollide(Urect urect) {
        if ((urect.getLeft() <= getLeft() || urect.getLeft() > getRight()) && (urect.getRight() <= getLeft() || urect.getRight() >= getRight())) {
            return false;
        }
        if (urect.getTop() <= getTop() || urect.getTop() > getBottom()) {
            return urect.getBottom() > getTop() && urect.getBottom() < getBottom();
        }
        return true;
    }

    public void Delete() {
        if (this.parent != null) {
            this.parent.DeleteChild(this);
        }
        if (list != null) {
            list.remove(this);
        }
        if (this.ClickDownlisteners != null) {
            this.ClickDownlisteners.clear();
        }
        if (this.ClickUplisteners != null) {
            this.ClickUplisteners.clear();
        }
        if (this.TouchMovelisteners != null) {
            this.TouchMovelisteners.clear();
        }
        if (this.UpdateListners != null) {
            this.UpdateListners.clear();
        }
    }

    public void clearChilds() {
        if (this.childrens != null) {
            for (int i = 0; i < this.childrens.size(); i++) {
                this.childrens.get(i).clearChilds();
                this.childrens.get(i).Delete();
            }
        }
    }
}
