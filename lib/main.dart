import 'package:flutter/material.dart';
import 'screens/home_screen.dart';
import 'screens/market_screen.dart';
import 'screens/my_act_screen.dart';
import 'screens/settings_screen.dart';
void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(

        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
      ),
      home: const DefaultPage(title: 'Flutter Demo Home Page'),
    );
  }
}

class DefaultPage extends StatefulWidget {
  const DefaultPage({super.key, required this.title});
  final String title;
  @override
  State<DefaultPage> createState() => _DefaultPageState();
}

class _DefaultPageState extends State<DefaultPage> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  static const List<BottomNavigationBarItem> _navItems = [
    BottomNavigationBarItem(
      icon: Icon(Icons.home_outlined),
      label: ''
    ),
    BottomNavigationBarItem(
      icon: Icon(Icons.bar_chart_outlined),
      label: '테스트2'
    ),
    BottomNavigationBarItem(
      icon: Icon(Icons.shopping_basket_outlined),
      label: 'd'
    ),
    BottomNavigationBarItem(
      icon: Icon(Icons.settings_outlined),
      label: '테스트d'
    )
  ];
  int _index = 0;
  @override
  void initState() {
    super.initState();

    _tabController = TabController(length: _navItems.length, vsync: this);
    _tabController.addListener(tabListener);
  }

  @override
  void dispose() {
    _tabController.removeListener(tabListener);
    super.dispose();
  }

  void tabListener() {
    setState(() {
      _index = _tabController.index;
    });
  }
  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: TabBarView(
        children: [
          HomeScreen(),
          MyActScreen(),
          MarketScreen(),
          SettingsScreen(),
        ],
        controller: _tabController,
      ),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        showSelectedLabels: false,
        showUnselectedLabels: false,
        onTap: (int index){
          _tabController.animateTo(index);
        },
        currentIndex: _index,
        items: _navItems
      ),

    );
  }
}
